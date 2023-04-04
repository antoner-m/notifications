package com.sitecenter.notification.repo;

import com.sitecenter.notification.data.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

        Optional<Notification> findByUuid(String uuid);

        @Query("SELECT COUNT(n) FROM Notification n WHERE n.done = false")
        Integer countNotDone();

        @Query("SELECT n FROM Notification n WHERE n.done = false")
        List <Notification> findNotDone();

        @Query("SELECT n FROM Notification n WHERE n.done = false")
        Page<Notification> findNotDone(Pageable pageable);

}