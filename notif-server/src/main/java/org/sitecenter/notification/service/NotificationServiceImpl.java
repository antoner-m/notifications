package org.sitecenter.notification.service;

import org.sitecenter.notification.data.*;
import org.sitecenter.notification.dto.NotificationShortDTO;
import org.sitecenter.notification.mapper.NotificationMapper;
import org.sitecenter.notification.repo.*;
import org.sitecenter.notification.service.events.AppEventInfo;
import org.sitecenter.notification.data.*;
import org.sitecenter.notification.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificationServiceImpl implements INotificationService {
    @Autowired
    NotificationRepo notificationRepo;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    NotificationForUserRepo notificationForUserRepo;
    @Autowired
    NotificationForUserGroupRepo notificationForUserGroupRepo;

    @Autowired
    IAppEventsPublisher appEventsPublisher;

    @Autowired
    UserRepo userRepo;
    @Autowired
    UserGroupRepo userGroupRepo;


    @Override
    @Transactional
    public Notification createNotification(NotificationShortDTO notificationShort) {
        Notification notification = new Notification();
        notificationMapper.updateFromDto(notification, notificationShort);
        notification.setId(null);
        notification.setUuid(UUID.randomUUID().toString());
        notification.setCreated(OffsetDateTime.now());
        notification.setModified(notification.getCreated());
        notification.setDone(false);
        notification.setDeleted(false);
        notificationRepo.save(notification);
        if (notificationShort.getUser_id() != null) {
            User user = getOrCreateUser(notificationShort.getUser_id());
            NotificationForUser nfUser = new NotificationForUser(notification, user);
            notificationForUserRepo.save(nfUser);

            AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.CREATED, AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_USER, nfUser.getId());
            appEventsPublisher.appNotification(eventInfo);

        } else if (notificationShort.getGroup_id() != null) {
            UserGroup userGroup = getOrCreateGroup(notificationShort.getGroup_id());

            NotificationForUserGroup nfUserGroup = new NotificationForUserGroup(notification, userGroup);
            notificationForUserGroupRepo.save(nfUserGroup);

            AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.CREATED, AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_GROUP, nfUserGroup.getId());
            appEventsPublisher.appNotification(eventInfo);
        }

        return notification;
    }

    @Override
    @Transactional
    public Optional<Notification> updateNotification(String uid, NotificationShortDTO notificationDto) {
        Optional<Notification> result = notificationRepo.findByUuid(uid);
        if (!result.isPresent()) return result;
        Notification notification = result.get();

        notificationMapper.updateFromDto(notification, notificationDto);
        notification.setModified(OffsetDateTime.now());
        notificationRepo.save(notification);

        AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.UPDATED, AppEventInfo.ENTITY_TYPE.NOTIFICATION, notification.getId());
        appEventsPublisher.appNotification(eventInfo);

        return result;
    }

    @Override
    public Optional<Notification> getNotification(String uid) {
        return notificationRepo.findByUuid(uid);
    }

    @Override
    @Transactional
    public Optional<NotificationForUser> viewed(String user_uid, String notification_uuid) {
        List<NotificationForUser> notification_list = notificationForUserRepo.findActiveByUuid(user_uid, notification_uuid);
        notification_list.forEach(nf -> {
            nf.setViewed(true);
            nf.setModified(OffsetDateTime.now());
            AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.VIEWED, AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_USER, nf.getId());
            appEventsPublisher.appNotification(eventInfo);
        });
        notificationForUserRepo.saveAll(notification_list);

        return notification_list.stream().findAny();
    }

    @Override
    @Transactional
    public List<NotificationForUser> viewedList(String user_uid, List<String> notification_uuid) {
        ArrayList<NotificationForUser> notification_list = new ArrayList<>();
        for (String uuid : notification_uuid) {
            List<NotificationForUser> notif = notificationForUserRepo.findActiveByUuid(user_uid, uuid);
            notification_list.addAll(notif);
        }

        notification_list.forEach(nf -> {
            nf.setViewed(true);
            nf.setModified(OffsetDateTime.now());
            AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.VIEWED, AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_USER, nf.getId());
            appEventsPublisher.appNotification(eventInfo);
        });
        notificationForUserRepo.saveAll(notification_list);

        return notification_list;
    }

    @Override
    @Transactional
    public List<NotificationForUser> viewed(String user_uid) {
        List<NotificationForUser> notificationsByUserUuid = notificationForUserRepo.findActiveByUserUuid(user_uid);
        notificationsByUserUuid.forEach(nf -> {
            nf.setViewed(true);
            nf.setModified(OffsetDateTime.now());
            AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.VIEWED, AppEventInfo.ENTITY_TYPE.NOTIFICATION_FOR_USER, nf.getId());
            appEventsPublisher.appNotification(eventInfo);
        });
        notificationForUserRepo.saveAll(notificationsByUserUuid);
        return notificationsByUserUuid;
    }


    @Override
    @Transactional
    public void done(String uid) {
        getNotification(uid).ifPresent(notification -> {
            notification.setDone(true);
            notification.setModified(OffsetDateTime.now());
            AppEventInfo eventInfo = new AppEventInfo("", AppEventInfo.EVENT_TYPE.UPDATED, AppEventInfo.ENTITY_TYPE.NOTIFICATION, notification.getId());
            appEventsPublisher.appNotification(eventInfo);
        });
    }

    @Override
    @Transactional
    public void deleteInternal(String uid) {
        getNotification(uid).ifPresent(notification -> {
            notification.setDeleted(true);
            notification.setModified(OffsetDateTime.now());
            AppEventInfo eventInfo = new AppEventInfo("",
                    AppEventInfo.EVENT_TYPE.DELETED, AppEventInfo.ENTITY_TYPE.NOTIFICATION, notification.getId());
            appEventsPublisher.appNotification(eventInfo);
        });
    }


    protected User getOrCreateUser(String user_uid) {
        User user = userRepo.findByUuid(user_uid).orElse(null);
        //create if not found
        if (user == null) {
            user = new User(user_uid);
            userRepo.save(user);
        }
        return user;
    }

    protected UserGroup getOrCreateGroup(String group_uid) {
        UserGroup userGroup = userGroupRepo.findByUuid(group_uid).orElse(null);
        //create if not found
        if (userGroup == null) {
            userGroup = new UserGroup(group_uid);
            userGroupRepo.save(userGroup);
        }
        return userGroup;
    }

}
