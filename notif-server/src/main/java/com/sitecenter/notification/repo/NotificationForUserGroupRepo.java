package com.sitecenter.notification.repo;

import com.sitecenter.notification.data.NotificationForUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationForUserGroupRepo extends JpaRepository<NotificationForUserGroup, Long> {

    @Query(value = "SELECT id from notification_for_group where processed = false and created < date_sub(now(), interval 1 minute) " +
                   "order by created LIMIT :size", nativeQuery = true)
    List<Long> findTopUndone(@Param("size") int size);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="1000")})
    Optional<NotificationForUserGroup> findWithLockingById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="1000")})
    @Query(value = "SELECT n from NotificationForUserGroup n where n.notification.uuid = :uuid")
    Optional<NotificationForUserGroup> findWithLockingByUUID(@Param("uuid") String uuid);

    @Query(value = "SELECT n from NotificationForUserGroup n where n.notification.uuid = :uuid")
    Optional<NotificationForUserGroup> findByUUID(@Param("uuid") String uuid);

}