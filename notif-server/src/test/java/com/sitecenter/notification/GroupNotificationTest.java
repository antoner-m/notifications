package com.sitecenter.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitecenter.notification.data.*;
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
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
        //(printOnlyOnFailure = true)
class GroupNotificationTest extends AbstractIntegrationTest {

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

    @Test
    void notificationCreate() throws Exception {
        ArrayList<UserGroupDTO> groups = new ArrayList<>(10);
        for (int i = 0; i < 10; i++)
            groups.add(new UserGroupDTO().setName("Group " + i).setUuid(UUID.randomUUID().toString()));

        List<UserGroupDTO> groups3 = groups.stream().limit(3).collect(Collectors.toList());

        ArrayList<UserDTO> users = new ArrayList<>(1000);
        for (int i = 1; i <= 100; i++)
            users.add(new UserDTO().setName("User " + i).setUuid(UUID.randomUUID().toString()).setGroups(groups3));

        log.info("Creating users in db via rest requests.");
        int i = 0;
        for (UserDTO user : users) {
            UserDTO resultDTO = createUser(user,objectMapper,mockMvc);
            String notification_uuid = resultDTO.getUuid();
            log.info("Created new user(" + (i++) + "):" + notification_uuid);
            Assertions.assertEquals(user.getUuid(), resultDTO.getUuid());
        }
//        clearCache();

        log.info("Checking db for stored users.");
        for (UserDTO user : users) {
            User dbuser = userRepo.findByUuid(user.getUuid()).orElseThrow(() -> new Exception("user not found:" + user.getUuid(), null));
            Assertions.assertEquals(user.getUuid(), dbuser.getUuid());
            Assertions.assertEquals(3, dbuser.getGroups().size());
        }

//		log.info("user_group contents:");
//		DebugUtil.printQuery(jdbcTemplate,"select * from user_group");
//		log.info("user_groups contents:");
//		 DebugUtil.printQuery(jdbcTemplate,"select * from user_groups");

        log.info("Checking db for stored usergroups.");
        for (UserGroupDTO group : groups3) {
            UserGroup dbgroup = userGroupRepo.findByUuid(group.getUuid()).orElseThrow(() -> new Exception("group not found:" + group.getUuid(), null));
            Assertions.assertEquals(group.getUuid(), dbgroup.getUuid());

            List<User> group_users = userRepo.findAllByGroupUuid(group.getUuid());

            Assertions.assertNotNull(group_users);
            Assertions.assertEquals(100, group_users.size());
        }
        String group_uuid = groups3.get(0).getUuid();

        NotificationDTO notificationShortDTO = sendNotificationForGroup(group_uuid, "Group title", objectMapper, mockMvc);
        String notification_uuid = notificationShortDTO.getUuid();
        log.debug("Created new group notification:" + notification_uuid);

        NotificationForUserGroup waitForDone = notificationForUserGroupRepo.findByUUID(notification_uuid).orElse(null);
        log.info("Waiting for processing...");
        int waitcycles = 1;
        while (waitForDone == null || !waitForDone.isProcessed()) {
            Thread.sleep(1000);
            waitForDone = notificationForUserGroupRepo.findByUUID(notification_uuid).orElse(null);
            log.info("Waiting for processing notification_uuid:{}... iteration:{}", notification_uuid, waitcycles++);
        }

        for (UserDTO user : users) {
            List<NotificationForUser> user_notifs = notificationForUserRepo.findByUserAndNotification(user.getUuid(), notification_uuid);
            Assertions.assertTrue(user_notifs.size() > 0);
        }
        log.info("Notifications {} found in db.", notification_uuid);
    }

    public static NotificationDTO sendNotificationForGroup(String group_uuid, String title, ObjectMapper objectMapper, MockMvc mockMvc) throws Exception {
        //Create Group Notification
        NotificationShortDTO dtoMultiThread = new NotificationShortDTO(title, "<h1>hi!</h1>", "");
        dtoMultiThread.setApp_id("junit").setGroup_id(group_uuid).setLevel(1);
        log.info("creating new group notification for stress multhithreading {}", dtoMultiThread.getTitle());

        String jsonStr = objectMapper.writeValueAsString(dtoMultiThread);

        // Send course as body to /api/notif/notifications
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/notif/notifications")
                .accept(MediaType.APPLICATION_JSON).content(jsonStr)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        NotificationDTO notificationShortDTO = objectMapper.readValue(response.getContentAsString(), NotificationDTO.class);
        String notification_uuid = notificationShortDTO.getUuid();
        log.debug("sendNotificationForGroup: Created new group notification {}", notification_uuid);

        return notificationShortDTO;
    }
    public static UserDTO createUser(UserDTO user, ObjectMapper objectMapper, MockMvc mockMvc) throws Exception {
        String jsonStr = objectMapper.writeValueAsString(user);
        // Send course as body to /students/Student1/courses
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/notif/users")
                .accept(MediaType.APPLICATION_JSON).content(jsonStr)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        UserDTO resultDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertEquals(user.getUuid(), resultDTO.getUuid());
        return resultDTO;
    }
}
