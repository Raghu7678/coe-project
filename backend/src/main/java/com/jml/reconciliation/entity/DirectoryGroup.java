package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "directory_groups")
public class DirectoryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "application_name", nullable = false, length = 50)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private PermissionLevel accessLevel;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public DirectoryGroup() {}

    public DirectoryGroup(String userId, String groupName, String applicationName, PermissionLevel accessLevel, LocalDateTime lastUpdated) {
        this.userId = userId;
        this.groupName = groupName;
        this.applicationName = applicationName;
        this.accessLevel = accessLevel;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public PermissionLevel getAccessLevel() { return accessLevel; }
    public void setAccessLevel(PermissionLevel accessLevel) { this.accessLevel = accessLevel; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
