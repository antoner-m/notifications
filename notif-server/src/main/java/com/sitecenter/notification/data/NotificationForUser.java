package com.sitecenter.notification.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;


/** For one @class Notification there will be one or many NotificationMsg to certain users.
 * For example: if we create notification to user group then there will be NotificationMsg for each user.
 *
  */
@Entity
@Table(name = "notification_for_user",
        indexes = {
                @Index(name = "ixNotifUserCreated", columnList = "created"),
                @Index(name = "ixNotifUserUserNotif", unique = true, columnList = "notification_id, user_id")
        })
@Data
@NoArgsConstructor
public class NotificationForUser {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    OffsetDateTime created;
    OffsetDateTime modified;

    @ManyToOne
    @JoinColumn(name = "notification_id")
    Notification notification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "viewed", nullable = false)
    boolean viewed = false;

    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    public NotificationForUser(Notification notification, User user) {
        this.notification = notification;
        this.user = user;
        this.created = OffsetDateTime.now();
        this.modified = this.created;
    }
}
