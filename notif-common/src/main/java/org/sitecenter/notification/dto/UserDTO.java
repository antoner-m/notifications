package org.sitecenter.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NonNull
    String uuid;

    String name;
    String email;

    String timezone;

    Map<String, String> properties = new HashMap<>();

    List<UserGroupDTO> groups = new ArrayList<>(1);
}