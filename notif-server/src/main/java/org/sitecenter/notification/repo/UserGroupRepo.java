package org.sitecenter.notification.repo;

import org.sitecenter.notification.data.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepo extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByUuid(String uuid);
}