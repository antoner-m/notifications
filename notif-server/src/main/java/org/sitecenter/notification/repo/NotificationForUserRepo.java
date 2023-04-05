package org.sitecenter.notification.repo;

import org.sitecenter.notification.data.NotificationForUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationForUserRepo extends PagingAndSortingRepository<NotificationForUser, Long> {
    @Query("SELECT n FROM NotificationForUser n WHERE n.user.id = :user_id")
    List<NotificationForUser> findByUser(@Param("user_id") Long user_id);

    @Query("SELECT n FROM NotificationForUser n JOIN n.user.groups g WHERE g.id = :usergroup_id and n.notification.id = :notification_id")
    List<NotificationForUser> findByNotificationAndUserGroup(@Param("notification_id")  Long notification_id, @Param("usergroup_id") Long usergroup_id);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.id = :user_id and n.notification.id = :notification_id")
    List<NotificationForUser> findByUserAndNotification(@Param("user_id") Long user_id, @Param("notification_id") Long notification_id);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.notification.uuid = :notification_uuid")
    List<NotificationForUser> findByUserAndNotification(@Param("user_uuid") String user_uuid, @Param("notification_uuid") String notification_uuid);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.notification.uuid = :notification_uuid  and n.viewed = false  and n.deleted = false and n.notification.deleted = false")
    List<NotificationForUser> findActiveByUserAndNotification(@Param("user_uuid") String user_uuid, @Param("notification_uuid") String notification_uuid);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.deleted = false and n.notification.deleted = false")
    List<NotificationForUser> findAllNotificationByUser(@Param("user_uuid") String user_uuid);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.deleted = false and n.notification.deleted = false")
    Page<NotificationForUser> findAllByUser(@Param("user_uuid") String user_uuid, Pageable pageable);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.viewed = false and n.deleted = false and n.notification.deleted = false")
    List<NotificationForUser> findActiveNotificationByUser(@Param("user_uuid") String user_uuid);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.viewed = false and n.deleted = false and n.notification.deleted = false")
    Page<NotificationForUser> findActiveNotificationByUser(@Param("user_uuid") String user_uuid, Pageable pageable);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.notification.uuid = :notification_uuid and n.viewed = false and n.deleted = false and n.notification.deleted = false")
    List<NotificationForUser> findActiveByUuid(@Param("user_uuid") String user_uuid, @Param("notification_uuid") String notification_uuid);

    @Query("SELECT n FROM NotificationForUser n WHERE n.user.uuid = :user_uuid and n.viewed = false and n.deleted = false and n.notification.deleted = false")
    List<NotificationForUser> findActiveByUserUuid(@Param("user_uuid") String user_uuid);

}