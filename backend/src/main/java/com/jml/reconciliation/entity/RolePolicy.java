package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "role_policies", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_name", "application_name"})
})
public class RolePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "application_name", nullable = false, length = 50)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "expected_permission", nullable = false, length = 20)
    private PermissionLevel expectedPermission;

    @Column(name = "is_required")
    private boolean required = true;

    public RolePolicy() {}

    public RolePolicy(String roleName, String applicationName, PermissionLevel expectedPermission, boolean required) {
        this.roleName = roleName;
        this.applicationName = applicationName;
        this.expectedPermission = expectedPermission;
        this.required = required;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public PermissionLevel getExpectedPermission() { return expectedPermission; }
    public void setExpectedPermission(PermissionLevel expectedPermission) { this.expectedPermission = expectedPermission; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
