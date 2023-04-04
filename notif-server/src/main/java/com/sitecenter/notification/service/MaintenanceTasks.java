package com.sitecenter.notification.service;

import com.sitecenter.notification.data.NotificationForUserGroup;
import com.sitecenter.notification.repo.NotificationForUserGroupRepo;
import com.sitecenter.notification.service.events.AppEventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Optional;

public class MaintenanceTasks {

    @Autowired
    NotificationForUserGroupRepo notificationForUserGroupRepo;
    @Autowired
    IAppEventsPublisher appEventsPublisher;

    @Scheduled(fixedDelay = 30000)
    /** Method scans for undone NotificationForUserGroup and creates notifications for each user in group.*/
    public void publishGroup(){
        List<Long> ids = notificationForUserGroupRepo.findTopUndone(100);
        for (Long id: ids) {
            Optional<NotificationForUserGroup> notifById = notificationForUserGroupRepo.findById(id);
            if (!notifById.isPresent() || notifById.get().isProcessed()) continue;
            AppEventInfo eventInfo = new AppEventInfo("MaintenanceTasks:unprocessed group found:"+id,
                    AppEventInfo.EVENT_TYPE.CREATED, AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_GROUP, id);
            appEventsPublisher.appNotification(eventInfo);
        }
    }
}
