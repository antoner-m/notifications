package org.sitecenter.notification.service;

import org.sitecenter.notification.data.UserGroup;
import org.sitecenter.notification.dto.UserGroupDTO;
import org.sitecenter.notification.mapper.UserGroupMapper;
import org.sitecenter.notification.mapper.UserMapper;
import org.sitecenter.notification.repo.UserGroupRepo;
import org.sitecenter.notification.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

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
