package com.sitecenter.notification.service.events;

import com.sitecenter.notification.data.NotificationForUserGroup;
import com.sitecenter.notification.repo.NotificationForUserGroupRepo;
import com.sitecenter.notification.service.INotificatonForGroupService;
import com.sitecenter.notification.service.events.AppEventInfo;
import com.sitecenter.notification.service.events.NotificationAppEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static com.sitecenter.notification.service.events.AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_GROUP;

@Component
@Slf4j
public class NotificationGroupEventsListener implements ApplicationListener<NotificationAppEvent> {
    @Autowired
    INotificatonForGroupService notificatonForGroupService;
    @Autowired
    NotificationForUserGroupRepo notificationForUserGroupRepo;

    @Override
    @Transactional
    @Async("listenerPoolTaskExecutor")
    public void onApplicationEvent(NotificationAppEvent event) {
        log.debug("listener received event {} ", event);
        AppEventInfo info = event.getInfo();
        //This listener works only with group notifications
        if (info.getEntityType() !=  NOTIFICATION_FOR_GROUP)
            return;
        onNotificationForGroupEvent(event.getInfo());
    }

    protected void onNotificationForGroupEvent(AppEventInfo info) {
        NotificationForUserGroup notificationForUserGroup = null;
        if (info.getEntity_id() != null) {
            notificationForUserGroup = notificationForUserGroupRepo.findById(info.getEntity_id()).orElse(null);
            log.debug("onNotificationForGroupEvent group findById: {}", notificationForUserGroup);
        } else if (info.getEntity_uuid() != null){
            notificationForUserGroup = notificationForUserGroupRepo.findByUUID(info.getEntity_uuid()).orElse(null);
            log.debug("onNotificationForGroupEvent group findByUUID: {}", notificationForUserGroup);
        }
        if (notificationForUserGroup == null)
            return;

        if (info.getEventType() == AppEventInfo.EVENT_TYPE.CREATED) {
            log.debug("Creating usermessages for group {}", notificationForUserGroup.getUserGroup().getUuid());
            notificatonForGroupService.createUserMessages(notificationForUserGroup);
        } else
        if (info.getEventType() == AppEventInfo.EVENT_TYPE.DELETED) {
            notificatonForGroupService.deleteUserMessages(notificationForUserGroup);
        }
    }
}