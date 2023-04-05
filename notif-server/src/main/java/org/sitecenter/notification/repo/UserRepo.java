package org.sitecenter.notification.repo;

import org.sitecenter.notification.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User, Long>, JpaRepository<User, Long> {
    Optional<User> findByUuid(String uuid);

    @Query("select u from User u inner join u.groups ug where ug.uuid = :group_uuid and ug.deleted = false and u.deleted = false")
    List<User> findAllByGroupUuid(@Param("group_uuid") String group_uuid);

    @Query("select u from User u inner join u.groups ug where ug.uuid = :group_uuid and ug.deleted = false and u.deleted = false")
    Page<User> findPageByGroup(@Param("group_uuid") String group_uuid, Pageable pageable);


    @Query("select u from User u inner join u.groups ug where ug.id = :group_id and ug.deleted = false and u.deleted = false")
    List<User> findAllByGroupId(@Param("group_id") Long group_id);
}