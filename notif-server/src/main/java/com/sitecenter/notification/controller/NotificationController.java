package com.sitecenter.notification.controller;

import com.sitecenter.notification.data.Notification;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.NotificationShortDTO;
import com.sitecenter.notification.mapper.NotificationMapper;
import com.sitecenter.notification.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/notif/notifications")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private INotificationService notificationService;


    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable(name = "id") String notification_uid) {
        Notification notification = notificationService.getNotification(notification_uid).orElse(null);
        if (notification == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found", null);
        if (notification.isDeleted())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found (deleted)", null);

        NotificationDTO notificationDto = notificationMapper.toDto(notification);

        return ResponseEntity.ok().body(notificationDto);
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationShortDTO notificationDto) {

        Notification notification = notificationService.createNotification(notificationDto);

        // convert entity to DTO
        NotificationDTO notificationDtoResult = notificationMapper.toDto(notification);

        return new ResponseEntity<>(notificationDtoResult, HttpStatus.CREATED);
    }

    // change the request for DTO
    // change the response for DTO
    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(@PathVariable String id, @RequestBody NotificationShortDTO notificationDto) {
        // convert DTO to Entity
        Optional <Notification> onotification = notificationService.updateNotification(id, notificationDto);
        if (!onotification.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found", null);

        Notification notification = onotification.get();
        if (notification.isDeleted())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found (deleted)", null);
                    // entity to DTO
        NotificationDTO notificationDtoResult = notificationMapper.toDto(notification);
        return ResponseEntity.ok().body(notificationDtoResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable(name = "id") String id) {
        notificationService.deleteInternal(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
