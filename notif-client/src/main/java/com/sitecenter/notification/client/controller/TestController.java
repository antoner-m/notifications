package com.sitecenter.notification.client.controller;

import com.sitecenter.notification.client.INotificationClient;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.NotificationShortDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


//test controller. Uncomment if you need it
//@RestController
@Slf4j
public class TestController {
    @Autowired
    @Qualifier("notifWebClient")
    private WebClient webClient;

    @Autowired
    INotificationClient notificationClient;

    @Value("${org.sitecenter.NOTIFICATION_API_URL}")
    final String USER_ID = "";

    @GetMapping("/test")
    public String test() {
        String result = webClient.get().uri("http://localhost:8091/api/notif/notifications/58256c57-9359-4bd1-8d06-370645127507")
                .retrieve().bodyToMono(String.class).block();
        log.info("retrieved notif:{}",result);

        log.info("creating new notification for user");
        NotificationShortDTO dto = new NotificationShortDTO("test from client","body of test","").setUser_id(USER_ID);
        NotificationDTO notification = notificationClient.createNotification(dto);
        log.info("Notification created {}", notification.toString());

        return notification.toString();
    }
}
