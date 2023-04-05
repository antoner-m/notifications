package org.sitecenter.notification.service.events;

import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
public class NotificationAppEvent  extends ApplicationEvent {
    private AppEventInfo info;

    public NotificationAppEvent(Object source, AppEventInfo info) {
        super(source);
        this.info = info;
    }
    public AppEventInfo getInfo() {
        return info;
    }
}