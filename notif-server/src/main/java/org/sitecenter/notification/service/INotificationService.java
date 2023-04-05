package org.sitecenter.notification.service;

import org.sitecenter.notification.data.Notification;
import org.sitecenter.notification.data.NotificationForUser;
import org.sitecenter.notification.dto.NotificationShortDTO;

import java.util.List;
import java.util.Optional;

public interface INotificationService {
    Notification createNotification(NotificationShortDTO notification);
    Optional<Notification> updateNotification(String uid, NotificationShortDTO notification);
    Optional<Notification> getNotification(String uid);

    Optional<NotificationForUser> viewed(String user_uid, String notification_uuid);
    List<NotificationForUser> viewedList(String user_uid, List <String> notification_uuid);
    List<NotificationForUser> viewed(String user_uid);
    void done(String uid);
    void deleteInternal(String uid);
}
