package com.sitecenter.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.data.NotificationForUserGroup;
import com.sitecenter.notification.data.User;
import com.sitecenter.notification.data.UserGroup;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.NotificationShortDTO;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserGroupDTO;
import com.sitecenter.notification.repo.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
        //(printOnlyOnFailure = true)
class GroupNotificationStressTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    NotificationRepo notificationRepo;

    @Autowired
    NotificationForUserRepo notificationForUserRepo;

    @Autowired
    NotificationForUserGroupRepo notificationForUserGroupRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserGroupRepo userGroupRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${org.sitecenter.tests.USER_ID}")
    String USER_ID = "";

//    @Autowired
//    private CacheManager cacheManager;
//    public void clearCache(){
//        for(String name:cacheManager.getCacheNames()){
//            cacheManager.getCache(name).clear();            // clear cache by name
//        }
//    }

    @Test
    void notificationCreate() throws Exception {
        ArrayList<UserGroupDTO> groups = new ArrayList<>(10);
        for (int i = 0; i < 10; i++)
            groups.add(new UserGroupDTO().setName("Group " + i).setUuid(UUID.randomUUID().toString()));

        ArrayList<UserDTO> users = new ArrayList<>(1000);
        for (int i = 1; i <= 100; i++)
            users.add(new UserDTO().setName("User " + i).setUuid(UUID.randomUUID().toString()).setGroups(groups));

        log.info("Creating users in db via rest requests.");
        for (UserDTO user : users) {
            UserDTO resultDTO = GroupNotificationTest.createUser(user,objectMapper,mockMvc);
//            log.info("User created:" + resultDTO.getUuid());
        }

        NotificationDTO lastCreatedNotification = null;

        int groupsNotif_count = 0;
        for (int group_i = 0; group_i < 30; group_i++)
            for (UserGroupDTO group : groups) {
                NotificationDTO notificationShortDTO = GroupNotificationTest.sendNotificationForGroup( group.getUuid(),"Mass group notify " + group_i, objectMapper, mockMvc);
//                log.info("Created mass notify {}:{}", group_i, notificationShortDTO.getUuid());
                lastCreatedNotification = notificationShortDTO;
                groupsNotif_count++;
            }

        String notification_uuid = lastCreatedNotification.getUuid();
        log.info("Created {} group notifications. last uid:{}", groupsNotif_count, notification_uuid);

        NotificationForUserGroup waitForDone = notificationForUserGroupRepo.findByUUID(notification_uuid).orElse(null);
        log.info("Waiting for processing...");
        int waitcycles = 1;
        while (waitForDone == null || !waitForDone.isProcessed()) {
            Thread.sleep(1000);
            waitForDone = notificationForUserGroupRepo.findByUUID(notification_uuid).orElse(null);
            log.info("Waiting for processing notification_uuid:{}... iteration number:{}", notification_uuid, waitcycles++);
        }

        for (UserDTO user : users) {
            List<NotificationForUser> user_notifs = notificationForUserRepo.findByUserAndNotification(user.getUuid(), notification_uuid);
            Assertions.assertTrue(user_notifs.size() == 1);
        }
        log.info("Notifications {} found in db.", notification_uuid);
    }

}
