package com.sitecenter.notification.controller;

import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.data.User;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.mapper.NotificationMapper;
import com.sitecenter.notification.mapper.UserMapper;
import com.sitecenter.notification.repo.NotificationForUserRepo;
import com.sitecenter.notification.service.INotificationService;
import com.sitecenter.notification.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
public class NotifServiceController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private INotificationService notificationService;
    @Autowired
    private NotificationForUserRepo notificationForUserRepo;

    @GetMapping(value = "/api/notif/users/{user_uuid}/notifications")
    public ResponseEntity<Page<NotificationDTO>> notificationListAll(@PathVariable(name = "user_uuid") String user_uuid,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                                     @RequestParam(value = "active", required = false, defaultValue = "0") Integer active) {
        if (page == null || size == null || size < 0) {
            page = 0;
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("notification.created").descending());
        Page<NotificationForUser> userNotificationsList;
        if (1 == active) userNotificationsList = notificationForUserRepo.findActiveNotificationByUser(user_uuid, pageable);
        else userNotificationsList = notificationForUserRepo.findAllByUser(user_uuid, pageable);

        Page<NotificationDTO> userNotificationsListDto = new PageImpl<>(notificationMapper.toDtoListWithUser(userNotificationsList.getContent()),
                pageable, userNotificationsList.getTotalElements());
        return ResponseEntity.ok().body(userNotificationsListDto);
    }

    //==================================================================================================================
    // Read and delete
    @PostMapping(value = "/api/notif/users/{user_uuid}/notifications/{notification_uuid}/markRead")
    public ResponseEntity<NotificationDTO> markRead(@PathVariable("user_uuid") String user_uuid,
                                                          @PathVariable("notification_uuid") String notification_uuid) {
        if (user_uuid == null || notification_uuid == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification id or user id not provided", null);

        NotificationForUser notificationForUser = notificationService.viewed(user_uuid, notification_uuid).orElse(null);
        if (notificationForUser == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found", null);

        NotificationDTO userNotificationsDto = notificationMapper.toDtoWithUser(notificationForUser);

        return ResponseEntity.ok().body(userNotificationsDto);
    }

    @PostMapping(value = "/api/notif/users/{user_uuid}/notifications/markRead")
    public ResponseEntity<List<NotificationDTO>> markReadList(@PathVariable("user_uuid") String user_uuid,
                                                          @RequestParam("notification_uuid") List<String> notification_uuid) {
        if (user_uuid == null || notification_uuid == null || notification_uuid.size() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification id or user id not provided", null);

        List<NotificationForUser> list_userNotification = notificationService.viewedList(user_uuid, notification_uuid);
        if (list_userNotification == null || list_userNotification.size() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found", null);

        List<NotificationDTO> userNotificationsDto = notificationMapper.toDtoListWithUser(list_userNotification);

        return ResponseEntity.ok().body(userNotificationsDto);
    }


    @PostMapping(value = "/api/notif/users/{user_uuid}/notifications/markAllRead")
    public ResponseEntity<List<NotificationDTO>> markReadAllByUser(@PathVariable("user_uuid") String user_uuid) {
        if (user_uuid == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id not provided", null);

        List<NotificationForUser> userNotificationsList = notificationService.viewed(user_uuid);
        List<NotificationDTO> userNotificationsDto = notificationMapper.toDtoListWithUser(userNotificationsList);

        return ResponseEntity.ok().body(userNotificationsDto);
    }

}