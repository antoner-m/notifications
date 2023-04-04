package com.sitecenter.notification.repo;

import com.sitecenter.notification.data.User;
import com.sitecenter.notification.data.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepo extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByUuid(String uuid);
}