package com.sitecenter.notification.controller;

import com.sitecenter.notification.data.User;
import com.sitecenter.notification.data.User;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.mapper.UserMapper;
import com.sitecenter.notification.mapper.UserMapper;
import com.sitecenter.notification.service.IUserService;
import com.sitecenter.notification.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/notif/users")
@Slf4j
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IUserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") String user_uid) {
        Optional<User> byExternalId = userService.get(user_uid);
        if (!byExternalId.isPresent())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found", null);
        User user = byExternalId.get();
        if (user.isDeleted())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found (deleted)", null);

        UserDTO userDto = userMapper.toDto(user);

        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {

        User notification = userService.create(userDto);

        // convert entity to DTO
        UserDTO userDtoResult = userMapper.toDto(notification);

        return new ResponseEntity<>(userDtoResult, HttpStatus.CREATED);
    }

    // change the request for DTO
    // change the response for DTO
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO userDto) {
        userDto.setUuid(id);
        // convert DTO to Entity
        User user = userService.update(userDto);
        // entity to DTO
        UserDTO userDtoResult = userMapper.toDto(user);
        return ResponseEntity.ok().body(userDtoResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") String id) {
        userService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
