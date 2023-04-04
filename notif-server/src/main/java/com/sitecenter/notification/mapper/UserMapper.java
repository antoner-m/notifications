package com.sitecenter.notification.mapper;

import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.data.User;
import com.sitecenter.notification.dto.NotificationDTO;
import com.sitecenter.notification.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserGroupMapper.class})
public interface UserMapper {
    UserDTO toDto(User source);
    User fromDto(UserDTO dto);
    void updateFromDto(@MappingTarget User target, UserDTO dto);

    List<UserDTO> toDtoList(List <User> source);
}
