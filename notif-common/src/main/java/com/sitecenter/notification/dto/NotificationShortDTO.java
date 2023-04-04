package com.sitecenter.notification.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class NotificationShortDTO {
    /** 0 - least urgent. 5 - most urgent */
    int level;
    /** Any string type of notification */
    String type;
    /** Creator unique application id */
    String app_id;

    /** domain of notifications. */
    String domain;

    /** can be null */
    String user_id;
    /** can be null */
    String group_id;

    String title;
    String html;
    String url;

    public NotificationShortDTO() {
    }
    public NotificationShortDTO(String title, String html, String url) {
        this.type = "";
        this.level = 0;
        this.title = title;
        this.html = html;
        this.url = url;
    }
}
