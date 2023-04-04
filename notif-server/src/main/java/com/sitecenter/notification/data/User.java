package com.sitecenter.notification.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user",
        indexes = {
                @Index(name = "ixUserEmail", columnList = "email"),
                @Index(name = "ixUserUuid", unique = true, columnList = "uuid")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String uuid;

    OffsetDateTime created;
    OffsetDateTime modified;

    String name;
    String email;

    String timezone;

    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @ElementCollection
    @CollectionTable(name = "user_properties", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    Map<String, String> properties;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id")
    )
    Set<UserGroup> groups = new HashSet<>();

    public User(String uuid) {
        this.uuid = uuid;
        this.created = OffsetDateTime.now();
        this.modified = this.created;
    }
}