package org.sitecenter.notification.mapper;

import org.sitecenter.notification.data.UserGroup;
import org.sitecenter.notification.dto.UserGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {
    UserGroupDTO toDto(UserGroup source);
    UserGroup fromDto(UserGroupDTO dto);
    void updateFromDto(@MappingTarget UserGroup target, UserGroupDTO dto);
}
