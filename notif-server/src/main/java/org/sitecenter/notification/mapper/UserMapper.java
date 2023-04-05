package org.sitecenter.notification.mapper;

import org.sitecenter.notification.data.User;
import org.sitecenter.notification.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserGroupMapper.class})
public interface UserMapper {
    UserDTO toDto(User source);
    User fromDto(UserDTO dto);
    void updateFromDto(@MappingTarget User target, UserDTO dto);

    List<UserDTO> toDtoList(List <User> source);
}
