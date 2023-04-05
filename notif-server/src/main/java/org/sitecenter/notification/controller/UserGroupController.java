package org.sitecenter.notification.controller;

import org.sitecenter.notification.data.UserGroup;
import org.sitecenter.notification.dto.UserGroupDTO;
import org.sitecenter.notification.mapper.UserGroupMapper;
import org.sitecenter.notification.service.IUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/notif/groups")
@Slf4j
public class UserGroupController {

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private IUserGroupService userGroupService;


    @GetMapping("/{id}")
    public ResponseEntity<UserGroupDTO> getUser(@PathVariable(name = "id") String usergroup_uid) {
        Optional<UserGroup> byExternalId = userGroupService.get(usergroup_uid);
        if (!byExternalId.isPresent())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User group not found", null);
        UserGroup userGroup= byExternalId.get();
        if (userGroup.isDeleted())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User group not found (deleted)", null);

        UserGroupDTO userGroupDto = userGroupMapper.toDto(userGroup);

        return ResponseEntity.ok().body(userGroupDto);
    }

    @PostMapping
    public ResponseEntity<UserGroupDTO> createUser(@RequestBody UserGroupDTO userGroupDto) {

        UserGroup userGroup = userGroupService.create(userGroupDto);

        // convert entity to DTO
        UserGroupDTO userDtoResult = userGroupMapper.toDto(userGroup);

        return new ResponseEntity<>(userDtoResult, HttpStatus.CREATED);
    }

    // change the request for DTO
    // change the response for DTO
    @PutMapping("/{id}")
    public ResponseEntity<UserGroupDTO> updateUserGroup(@PathVariable String uuid, @RequestBody UserGroupDTO userDto) {
        // convert DTO to Entity
        userDto.setUuid(uuid);
        UserGroup userGroup = userGroupService.update(userDto);
        // entity to DTO
        UserGroupDTO userDtoResult = userGroupMapper.toDto(userGroup);
        return ResponseEntity.ok().body(userDtoResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserGroup(@PathVariable(name = "id") String id) {
        userGroupService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
