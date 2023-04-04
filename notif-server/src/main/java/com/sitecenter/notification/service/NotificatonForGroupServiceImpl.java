package com.sitecenter.notification.service;

import com.sitecenter.notification.data.*;
import com.sitecenter.notification.repo.NotificationForUserGroupRepo;
import com.sitecenter.notification.repo.NotificationForUserRepo;
import com.sitecenter.notification.repo.UserGroupRepo;
import com.sitecenter.notification.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@Slf4j
public class NotificatonForGroupServiceImpl implements INotificatonForGroupService{
    final NotificationForUserRepo notificationForUserRepo;
    final NotificationForUserGroupRepo notificationForUserGroupRepo;

    final UserGroupRepo userGroupRepo;
    final UserRepo userRepo;

    public NotificatonForGroupServiceImpl(NotificationForUserRepo notificationForUserRepo, NotificationForUserGroupRepo notificationForUserGroupRepo, UserGroupRepo userGroupRepo, UserRepo userRepo) {
        this.notificationForUserRepo = notificationForUserRepo;
        this.notificationForUserGroupRepo = notificationForUserGroupRepo;
        this.userGroupRepo = userGroupRepo;
        this.userRepo = userRepo;
    }


    @Override
    @Transactional
    public void createUserMessages(final NotificationForUserGroup notificationForGroup) {
        log.debug("createUserMessages for notificationForGroup {}",notificationForGroup);
        NotificationForUserGroup notifWithLock = notificationForUserGroupRepo.findWithLockingById(notificationForGroup.getId()).orElse(null);
        if (notifWithLock == null || notifWithLock.getUserGroup() == null || notifWithLock.getNotification() == null) {
            log.error("notifWithLock is null. createUserMessages for notificationForGroup {}",notificationForGroup);
            return;
        }
        UserGroup ugroup = userGroupRepo.findById(notifWithLock.getUserGroup().getId()).orElse(null);
        if (ugroup == null) {
            log.error("ugroup is null. createUserMessages for notificationForGroup {}", notificationForGroup);
            return;
        }

        Long notification_id = notifWithLock.getNotification().getId();
        Notification notification = notifWithLock.getNotification();

        List<User> group_users = userRepo.findAllByGroupUuid(ugroup.getUuid());
        log.debug("createUserMessages users found {} for notificationForGroup {} ", group_users == null ? 0 : group_users.size(), notificationForGroup);

        for (User user : group_users) {
            List<NotificationForUser> byUserAndNotification = notificationForUserRepo.findByUserAndNotification(user.getId(), notification_id);
            if (byUserAndNotification == null || byUserAndNotification.size() == 0) {
                NotificationForUser nfUser = new NotificationForUser(notification, user);
                notificationForUserRepo.save(nfUser);
                log.debug("Created new user notification {}, main notification {}, user {}, usergroup {}",nfUser.getId(), notification.getUuid(), user.getUuid(),ugroup.getUuid());
            }
        }
        notifWithLock.setModified(OffsetDateTime.now());
        notifWithLock.setProcessed(true);
    }

    @Override
    @Transactional
    public void deleteUserMessages(NotificationForUserGroup notificationForGroup) {
        log.debug("deleteUserMessages for notificationForGroup {}",notificationForGroup);
        NotificationForUserGroup notifWithLock = notificationForUserGroupRepo.findWithLockingById(notificationForGroup.getId()).orElse(null);
        if (notifWithLock == null || notifWithLock.getUserGroup() == null || notifWithLock.getNotification() == null)
            return;
        Long ugroup_id = notifWithLock.getUserGroup().getId();
        Long notification_id = notificationForGroup.getNotification().getId();

        List<NotificationForUser> byNotificationAndUserGroup = notificationForUserRepo.findByNotificationAndUserGroup(notification_id, ugroup_id);
        for (NotificationForUser nfuser : byNotificationAndUserGroup) {
            if (!nfuser.isDeleted()) {
                nfuser.setModified(OffsetDateTime.now());
                nfuser.setDeleted(true);
            }
        }
    }
}
