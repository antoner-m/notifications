package com.sitecenter.notification.service;

import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.data.User;
import com.sitecenter.notification.data.UserGroup;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserGroupDTO;
import com.sitecenter.notification.mapper.UserMapper;
import com.sitecenter.notification.repo.NotificationForUserRepo;
import com.sitecenter.notification.repo.NotificationRepo;
import com.sitecenter.notification.repo.UserGroupRepo;
import com.sitecenter.notification.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements IUserService{
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserGroupRepo userGroupRepo;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    NotificationForUserRepo notificationForUserRepo;
    @Autowired
    NotificationRepo notificationRepo;

    @Override
    @Transactional
    public User create(UserDTO userDto) {
//        userRepo.
        User user = new User(null);
        userMapper.updateFromDto(user, userDto);
        user.setGroups(new HashSet<>());
        user.setId(null);
        for (UserGroupDTO groupDto : userDto.getGroups()) {
            String group_uid = groupDto.getUuid();
            UserGroup group = userGroupRepo.findByUuid(group_uid).orElse(null);
            if (group == null) {
                group = new UserGroup(groupDto.getUuid(), groupDto.getName());
                userGroupRepo.save(group);
            }
            user.getGroups().add(group);
        }
        userRepo.saveAndFlush(user);
        return user;
    }

    @Override
    @Transactional
    public User update(UserDTO userDto) {
        User user = userRepo.findByUuid(userDto.getUuid()).orElse(null);
        if (user == null) return null;

        userMapper.updateFromDto(user, userDto);
        user.setModified(OffsetDateTime.now());
        userRepo.save(user);
        int userGroupsCount = user.getGroups() == null ? 0 : user.getGroups().size();
        int userDtoGroupsCount = userDto.getGroups() == null ? 0 : userDto.getGroups().size();

        if (!(userGroupsCount == 0 && userDtoGroupsCount == 0)) {
            Set<String> ugDtoUuid = userDto.getGroups().stream().map(UserGroupDTO::getUuid).collect(Collectors.toSet());

            Set<UserGroup> removedOldGroup = user.getGroups().stream().filter(g -> ugDtoUuid.contains(g.getUuid())).collect(Collectors.toSet());
            user.setGroups(removedOldGroup);
            for (UserGroupDTO groupDto : userDto.getGroups()) {
                String group_uid = groupDto.getUuid();
                UserGroup group = userGroupRepo.findByUuid(group_uid).orElse(new UserGroup(groupDto.getUuid(), groupDto.getName()));
                userGroupRepo.save(group);
                user.getGroups().add(group);
            }
            user.setModified(OffsetDateTime.now());
            userRepo.save(user);
        }
        return user;
    }

    @Override
    public Optional<User>  get(String uid) {
        return userRepo.findByUuid(uid);
    }

    @Override
    @Transactional
    public void delete(String uid) {
        Optional<User> ouser = get(uid);
        if (!ouser.isPresent()) return;
        User user = ouser.get();
        user.setDeleted(true);
        user.setModified(OffsetDateTime.now());

        List<NotificationForUser> notificationForUsers = notificationForUserRepo.findByUser(user.getId());
        notificationForUsers.forEach(n -> {n.getNotification().setDeleted(true);notificationRepo.save(n.getNotification());});
    }

    @Override
    @Transactional
    public User addUserToGroup(String user_uid, String group_uid) {
        if (user_uid == null || group_uid == null) return null;
        User user = get(user_uid).orElse(null);

        if (user == null) return null;

        boolean found = user.getGroups().stream().anyMatch(g -> group_uid.equals(g.getUuid()));
        if (!found) {
            UserGroup ug = userGroupRepo.findByUuid(group_uid).orElse(new UserGroup(group_uid, ""));
            userGroupRepo.save(ug);
            user.getGroups().add(ug);
            user.setModified(OffsetDateTime.now());
        }
        return user;
    }

    @Override
    @Transactional
    public User removeUserFromGroup(String user_uid, String group_uid) {
        User user = get(user_uid).orElse(null);
        if (user == null) return null;

        user.getGroups().stream().filter(g -> group_uid.equals(g.getUuid())).findAny().ifPresent(removeGroup -> user.getGroups().remove(removeGroup));
        user.setModified(OffsetDateTime.now());
        return user;
    }
}
