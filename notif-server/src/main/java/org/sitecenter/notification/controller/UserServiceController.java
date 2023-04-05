package org.sitecenter.notification.controller;

import org.sitecenter.notification.data.User;
import org.sitecenter.notification.dto.UserDTO;
import org.sitecenter.notification.mapper.NotificationMapper;
import org.sitecenter.notification.mapper.UserMapper;
import org.sitecenter.notification.repo.UserRepo;
import org.sitecenter.notification.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserServiceController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserRepo userRepo;


    //==================================================================================================================
    // Users and groups
    @PostMapping(value = "/api/notif/users/{user_uuid}/groups/{group_uuid}/add")
    public ResponseEntity<UserDTO> addUserToGroup(@PathVariable("user_uuid") String user_uuid,
                                                  @PathVariable("group_uuid") String group_uuid) {
        User user = userService.addUserToGroup(user_uuid, group_uuid);
        UserDTO userDto = userMapper.toDto(user);

        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping(value = "/api/notif/users/{user_uuid}/groups/{group_uuid}/remove")
    public ResponseEntity<UserDTO> removeUserFromGroup(@PathVariable("user_uuid") String user_uuid,
                                                       @PathVariable("group_uuid") String group_uuid) {
        User user = userService.removeUserFromGroup(user_uuid, group_uuid);
        UserDTO userDto = userMapper.toDto(user);

        return ResponseEntity.ok().body(userDto);
    }
    @GetMapping(value = "/api/notif/groups/{group_uuid}/users")
    public ResponseEntity<Page<UserDTO>> listUsersInGroup(@PathVariable("group_uuid") String group_uuid,
                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                @RequestParam(value = "size", required = false, defaultValue = "100") Integer size) {

            Pageable pageable = PageRequest.of(page, size);
            Page<User> userList = userRepo.findPageByGroup(group_uuid, pageable);

            Page<UserDTO> userListDto = new PageImpl<UserDTO>(userMapper.toDtoList(userList.getContent()),
                    pageable, userList.getTotalElements());
            return ResponseEntity.ok().body(userListDto);
        }
}