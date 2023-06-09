package org.sitecenter.notification.client;

import org.sitecenter.notification.client.dto.UserDTOPageableResponse;
import org.sitecenter.notification.dto.UserDTO;
import org.springframework.data.domain.Pageable;

public interface INotifUserClient {
    UserDTO createUser(UserDTO user);
    UserDTO getUser(String user_uid);
    UserDTO updateUser(UserDTO userDTO);
    void deleteUser(String user_uuid);

    UserDTOPageableResponse listUsersInGroup(String group_uuid, Pageable pageable);

    UserDTO addUserToGroup(String user_uid, String group_uid);
    UserDTO removeUserFromGroup(String user_uid, String group_uid);
}
