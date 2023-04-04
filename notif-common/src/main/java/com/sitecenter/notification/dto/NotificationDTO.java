package com.sitecenter.notification.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class NotificationDTO {
    /** Guid of notification. */
    private String uuid;

    OffsetDateTime created;
    OffsetDateTime modified;

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

    /** All messages to users were created */
    boolean done;

    /** This message was read by user.*/
    boolean viewed;

    public NotificationDTO() {
    }
    public NotificationDTO(String title, String html, String url) {
        this.created = OffsetDateTime.now();
        this.modified = this.created;
        this.type = "";
        this.level = 0;
        this.title = title;
        this.html = html;
        this.url = url;
    }
}
