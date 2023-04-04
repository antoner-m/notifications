package com.sitecenter.notification.service;

import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.data.User;
import com.sitecenter.notification.data.UserGroup;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserGroupDTO;
import com.sitecenter.notification.mapper.UserGroupMapper;
import com.sitecenter.notification.mapper.UserMapper;
import com.sitecenter.notification.repo.NotificationForUserRepo;
import com.sitecenter.notification.repo.NotificationRepo;
import com.sitecenter.notification.repo.UserGroupRepo;
import com.sitecenter.notification.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserGroupServiceImpl implements IUserGroupService{
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserGroupRepo userGroupRepo;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserGroupMapper userGroupMapper;
    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public UserGroup create(UserGroupDTO userGroupDto) {
        UserGroup userGroup = new UserGroup(null);
        userGroupMapper.updateFromDto(userGroup, userGroupDto);
        userGroup.setId(null);
        userGroupRepo.saveAndFlush(userGroup);
        return userGroup;
    }

    @Override
    @Transactional
    public UserGroup update(UserGroupDTO userGroupDTO) {
        UserGroup userGroup = userGroupRepo.findByUuid(userGroupDTO.getUuid()).orElse(null);
        if (userGroup == null) return null;

        userGroupMapper.updateFromDto(userGroup, userGroupDTO);
        userGroup.setModified(OffsetDateTime.now());
        userGroupRepo.save(userGroup);
        return userGroup;
    }

    @Override
    public Optional<UserGroup>  get(String uid) {
        return userGroupRepo.findByUuid(uid);
    }

    @Override
    @Transactional
    public void delete(String uid) {
        Optional<UserGroup> ouser = get(uid);
        if (!ouser.isPresent()) return;
        UserGroup userGroup = ouser.get();
        if (userGroup.isDeleted()) return;
        userGroup.setDeleted(true);
        userGroup.setModified(OffsetDateTime.now());
    }
}
