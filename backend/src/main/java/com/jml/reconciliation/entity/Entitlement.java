package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entitlements")
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "application_name", nullable = false, length = 50)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false, length = 20)
    private PermissionLevel permissionLevel;

    @Column(name = "granted_date", nullable = false)
    private LocalDateTime grantedDate;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, REVOKED, EXPIRED

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public Entitlement() {}

    public Entitlement(String userId, String applicationName, PermissionLevel permissionLevel, LocalDateTime grantedDate, String status, LocalDateTime lastUpdated) {
        this.userId = userId;
        this.applicationName = applicationName;
        this.permissionLevel = permissionLevel;
        this.grantedDate = grantedDate;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }

    public LocalDateTime getGrantedDate() { return grantedDate; }
    public void setGrantedDate(LocalDateTime grantedDate) { this.grantedDate = grantedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
