package org.sitecenter.notification.service;

import org.sitecenter.notification.data.UserGroup;
import org.sitecenter.notification.dto.UserGroupDTO;

import java.util.Optional;

public interface IUserGroupService {
    UserGroup create(UserGroupDTO userGroupDTO);
    UserGroup update(UserGroupDTO userGroupDTO);
    Optional<UserGroup> get(String uid);
    void delete(String uid);
}
