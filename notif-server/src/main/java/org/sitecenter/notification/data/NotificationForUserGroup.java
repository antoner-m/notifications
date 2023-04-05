package org.sitecenter.notification.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;


/** For one @class Notification there will be one or many NotificationMsg to certain users.
 * For example: if we create notification to user group then there will be NotificationMsg for each user.
 *
  */
@Entity
@Table(name = "notification_for_group",
        indexes = {
                @Index(name = "ixNotifGroupCreated", columnList = "created"),
                @Index(name = "ixNotifGroupGroupNotif", unique = true, columnList = "notification_id, usergroup_id")
        })
@Data
@NoArgsConstructor
public class NotificationForUserGroup {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    OffsetDateTime created;
    OffsetDateTime modified;

    @ManyToOne
    @JoinColumn(name = "notification_id")
    Notification notification;

    @ManyToOne
    @JoinColumn(name = "usergroup_id")
    UserGroup userGroup;

    /** All individual NotificationForUser were created */
    @Column(name = "processed", nullable = false)
    boolean processed = false;

    public NotificationForUserGroup(Notification notification, UserGroup group) {
        this.notification = notification;
        this.userGroup = group;
        this.created = OffsetDateTime.now();
        this.modified = this.created;
    }

}
