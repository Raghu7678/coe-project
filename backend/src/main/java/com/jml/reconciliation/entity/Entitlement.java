package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "entitlements")
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String appName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionLevel permissionLevel;

    public Entitlement() {}

    public Entitlement(String username, String appName, PermissionLevel permissionLevel) {
        this.username = username;
        this.appName = appName;
        this.permissionLevel = permissionLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }
}
