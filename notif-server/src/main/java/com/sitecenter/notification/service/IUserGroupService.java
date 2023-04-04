package com.sitecenter.notification.service;

import com.sitecenter.notification.data.User;
import com.sitecenter.notification.data.UserGroup;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserGroupDTO;

import java.util.Optional;

public interface IUserGroupService {
    UserGroup create(UserGroupDTO userGroupDTO);
    UserGroup update(UserGroupDTO userGroupDTO);
    Optional<UserGroup> get(String uid);
    void delete(String uid);
}
