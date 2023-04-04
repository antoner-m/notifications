package com.sitecenter.notification.mapper;

import com.sitecenter.notification.data.Notification;
import com.sitecenter.notification.data.NotificationForUser;
import com.sitecenter.notification.dto.NotificationShortDTO;
import com.sitecenter.notification.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDTO toDto(Notification source);
    List<NotificationDTO> toDtoList(List <Notification> source);

    @Mapping( target = ".", source = "notification" )
    @Mapping( target = "user_id", source = "user.uuid" )
    NotificationDTO toDtoWithUser(NotificationForUser source);

    List<NotificationDTO> toDtoListWithUser(List <NotificationForUser> source);

    Notification fromDto(NotificationDTO dto);
    Notification fromDto(NotificationShortDTO dto);
    void updateFromDto(@MappingTarget Notification target, NotificationShortDTO dto);

}
