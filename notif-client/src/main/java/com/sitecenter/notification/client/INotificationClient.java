package com.sitecenter.notification.client;

import com.sitecenter.notification.client.dto.NotificationDTOPageableResponse;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.NotificationShortDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface INotificationClient {
    NotificationDTO createNotification(NotificationShortDTO notification);
    NotificationDTO getNotification(String uid);

    NotificationDTOPageableResponse getActiveByUser(String user_uid);
    NotificationDTOPageableResponse getAllByUser(String user_uid, Pageable pageable);

    Optional<NotificationDTO> markRead(String user_uid, String notification_uuid);
    List<NotificationDTO> markReadList(String user_uid, List <String> notification_uuid);
    List<NotificationDTO> markReadByUser(String user_uid);
}
