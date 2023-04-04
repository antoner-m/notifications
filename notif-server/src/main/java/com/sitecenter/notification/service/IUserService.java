package com.sitecenter.notification.service;

import com.sitecenter.notification.data.User;
import com.sitecenter.notification.dto.UserDTO;

import java.util.Optional;

public interface IUserService {
    User create(UserDTO notification);
    User update(UserDTO userDto);
    Optional<User> get(String uid);
    void delete(String uid);

    User addUserToGroup(String user_uid, String group_uid);
    User removeUserFromGroup(String user_uid, String group_uid);
}
