package com.sitecenter.notification.service;

import com.sitecenter.notification.data.Notification;
import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.data.NotificationForUserGroup;
import com.sitecenter.notification.service.events.AppEventInfo;
import com.sitecenter.notification.service.events.NotificationAppEvent;

public interface IAppEventsPublisher {
    void appNotification(AppEventInfo event);
}
