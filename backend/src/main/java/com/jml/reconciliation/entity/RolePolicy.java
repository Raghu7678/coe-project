package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "role_policies")
public class RolePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roleName;

    @Column(nullable = false)
    private String appName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionLevel requiredPermission;

    private Boolean mandatory;

    public RolePolicy() {}

    public RolePolicy(String roleName, String appName, PermissionLevel requiredPermission, Boolean mandatory) {
        this.roleName = roleName;
        this.appName = appName;
        this.requiredPermission = requiredPermission;
        this.mandatory = mandatory;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public PermissionLevel getRequiredPermission() { return requiredPermission; }
    public void setRequiredPermission(PermissionLevel requiredPermission) { this.requiredPermission = requiredPermission; }

    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
}
