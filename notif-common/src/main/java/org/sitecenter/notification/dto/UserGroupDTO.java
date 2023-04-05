package org.sitecenter.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@NoArgsConstructor
@RequiredArgsConstructor
public class UserGroupDTO {
    @NonNull
    String uuid;
    String name;
}
