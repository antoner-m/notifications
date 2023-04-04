package com.sitecenter.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatewayInfoDTO {
    String url;
    String uuid;
    String name;
}
