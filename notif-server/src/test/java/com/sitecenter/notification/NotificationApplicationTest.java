package com.sitecenter.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitecenter.notification.data.Notification;
import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.NotificationShortDTO;
import com.sitecenter.notification.repo.NotificationForUserRepo;
import com.sitecenter.notification.repo.NotificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.ServletContext;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
class NotificationApplicationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    NotificationRepo notificationRepo;

    @Autowired
    NotificationForUserRepo notificationForUserRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() {

    }

    @Value("${org.sitecenter.tests.USER_ID}")
    String USER_ID = "";

    @Test
    void notificationCreate() throws Exception {
        NotificationShortDTO dto = new NotificationShortDTO("junit title", "<h1>hi!</h1>", "");
        dto.setApp_id("junit").setUser_id(USER_ID).setLevel(1);
        NotificationDTO notificationShortDTO = createNotification(dto);

        String notification_uuid = notificationShortDTO.getUuid();
        log.info("Created new notification:" + notification_uuid);

        Notification notification = notificationRepo.findByUuid(notification_uuid).orElse(null);
        Assertions.assertNotNull(notification);
        log.info("Found new notification in db id:" + notification.getId());

        List<NotificationForUser> activeByUserUuid = notificationForUserRepo.findActiveByUserAndNotification(USER_ID, notification_uuid);
        Assertions.assertTrue(activeByUserUuid.size() == 1);
        log.info("Found new notification in notificationForUser");
        NotificationForUser nfUser = activeByUserUuid.get(0);

        Assertions.assertEquals(nfUser.getNotification().getId(), notification.getId());
        Assertions.assertEquals(nfUser.getNotification().getApp_id(), dto.getApp_id());

        readUserNotification(nfUser.getUser().getUuid(), nfUser.getNotification().getUuid());

        List<NotificationForUser> activeAfterRead = notificationForUserRepo.findActiveByUserAndNotification(USER_ID, notification_uuid);
        Assertions.assertTrue(activeAfterRead.size() == 0);
        log.info("Read notification successful.");

        for (int i = 0; i < 3; i++) {
            NotificationShortDTO newNotifDTO = new NotificationShortDTO("junit title test "+i, "<h1>hi "+i+"!</h1>", "");
            newNotifDTO.setApp_id("junit").setUser_id(USER_ID).setLevel(1);

            createNotification(dto);
        }

        List<NotificationForUser> activeAfterCreate = notificationForUserRepo.findActiveByUserUuid(USER_ID);
        Assertions.assertTrue(activeAfterCreate.size() == 3);
        log.info("Create 3 new notifications.");

        readAllUserNotification(USER_ID);

        List<NotificationForUser> activeAfterReadAll = notificationForUserRepo.findActiveByUserUuid(USER_ID);
        Assertions.assertTrue(activeAfterReadAll.size() == 0);
        log.info("Read all notifications from user successful.");
    }

    public NotificationDTO createNotification(NotificationShortDTO dto) throws Exception {
        String jsonStr = objectMapper.writeValueAsString(dto);

        // Send course as body to /api/notif/notifications
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/notif/notifications")
                .accept(MediaType.APPLICATION_JSON).content(jsonStr)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        NotificationDTO notificationShortDTO = objectMapper.readValue(response.getContentAsString(), NotificationDTO.class);
        return notificationShortDTO;
    }

    public void readUserNotification(String user_uuid, String notification_uuid) throws Exception {
        // Send course as body to /api/notif/notifications
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/notif/users/" + user_uuid + "/markRead")
                .param("notification_uuid", notification_uuid)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    public void readAllUserNotification(String user_uuid) throws Exception {
        // Send course as body to /api/notif/notifications
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/notif/users/" + user_uuid + "/markAllRead")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

}
