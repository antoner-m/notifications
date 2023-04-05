package org.sitecenter.notification.data;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "user_group",
        indexes = {
                @Index(name = "ixUserGroupUuid", unique = true, columnList = "uuid")
        })
public class UserGroup {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    OffsetDateTime created;
    OffsetDateTime modified;

    @Column(nullable = false, unique = true)
    String uuid;
    @Column
    String name;

    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @ToString.Exclude
    @ManyToMany(mappedBy = "groups")
    List<User> users = new ArrayList<>();

    public UserGroup() {
    }

    public UserGroup(String uuid) {
        this.uuid = uuid;
        this.created = OffsetDateTime.now();
        this.modified = this.created;
    }
    public UserGroup(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.created = OffsetDateTime.now();
        this.modified = this.created;
    }
}
