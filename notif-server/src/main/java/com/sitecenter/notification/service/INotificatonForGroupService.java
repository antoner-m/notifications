package com.sitecenter.notification.service;

import com.sitecenter.notification.data.NotificationForUserGroup;

public interface INotificatonForGroupService {
    void createUserMessages(NotificationForUserGroup notificationForGroup);
    void deleteUserMessages(NotificationForUserGroup notificationForGroup);
}
