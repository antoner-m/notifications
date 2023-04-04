package com.sitecenter.notification.data;

import com.sitecenter.notification.dto.NotificationDTO;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notification",
        indexes = {
                @Index(name = "ixNotificationUndone", columnList = "done"),
                @Index(name = "ixNotificationExternalId", unique = true, columnList = "uuid")
        })
@Data

public class Notification {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    /* External guid of notification. */
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

    String title;
    String html;
    String url;

    /** All notification messages to users were created */
    @Column(name = "done", nullable = false)
    boolean done = false;

    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    public Notification() {
    }
    public Notification(String title, String html, String url) {
        this.created = OffsetDateTime.now();
        this.modified = created;
        this.type = "";
        this.level = 0;
        this.title = title;
        this.html = html;
        this.url = url;
    }
}
