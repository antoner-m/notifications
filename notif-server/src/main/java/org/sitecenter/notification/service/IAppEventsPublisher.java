package org.sitecenter.notification.service;

import org.sitecenter.notification.service.events.AppEventInfo;

public interface IAppEventsPublisher {
    void appNotification(AppEventInfo event);
}
