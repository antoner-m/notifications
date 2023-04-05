package org.sitecenter.notification.service;

import org.sitecenter.notification.data.NotificationForUserGroup;

public interface INotificatonForGroupService {
    void createUserMessages(NotificationForUserGroup notificationForGroup);
    void deleteUserMessages(NotificationForUserGroup notificationForGroup);
}
