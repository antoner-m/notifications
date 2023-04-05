package org.sitecenter.notification.service;

import org.sitecenter.notification.service.events.AppEventInfo;
import org.sitecenter.notification.service.events.NotificationAppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AppEventsPublisherImpl implements IAppEventsPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void appNotification(AppEventInfo event) {
        NotificationAppEvent appEvent = new NotificationAppEvent(this, event);
        applicationEventPublisher.publishEvent(appEvent);
    }
}
