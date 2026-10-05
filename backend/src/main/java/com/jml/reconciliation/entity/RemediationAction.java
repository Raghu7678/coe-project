package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import com.jml.reconciliation.model.enums.RemediationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "remediation_actions")
public class RemediationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long issueId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String fullName;

    private String actionType; // REVOKE_ORPHANED_ACCESS, DOWNGRADE_EXCESSIVE_PRIVILEGES, REVOKE_UNAPPROVED_ACCESS, GRANT_MISSING_REQUIRED_ACCESS

    private String appName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RemediationStatus status;

    @Enumerated(EnumType.STRING)
    private PermissionLevel previousPermissionLevel;

    @Enumerated(EnumType.STRING)
    private PermissionLevel targetPermissionLevel;

    @Column(length = 2000)
    private String rationale;

    private String initiatedBy;

    private String approvedBy;

    private LocalDateTime createdAt;

    private LocalDateTime executedAt;

    private LocalDateTime rolledBackAt;

    private Boolean stakeholderValidated = false;

    private String validatedBy;

    @Column(length = 2000)
    private String validationNotes;

    private LocalDateTime validatedAt;

    public RemediationAction() {}

    public RemediationAction(Long issueId, String username, String fullName, String actionType, String appName, RemediationStatus status, PermissionLevel previousPermissionLevel, PermissionLevel targetPermissionLevel, String rationale, String initiatedBy) {
        this.issueId = issueId;
        this.username = username;
        this.fullName = fullName;
        this.actionType = actionType;
        this.appName = appName;
        this.status = status;
        this.previousPermissionLevel = previousPermissionLevel;
        this.targetPermissionLevel = targetPermissionLevel;
        this.rationale = rationale;
        this.initiatedBy = initiatedBy;
        this.createdAt = LocalDateTime.now();
        this.stakeholderValidated = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public RemediationStatus getStatus() { return status; }
    public void setStatus(RemediationStatus status) { this.status = status; }

    public PermissionLevel getPreviousPermissionLevel() { return previousPermissionLevel; }
    public void setPreviousPermissionLevel(PermissionLevel previousPermissionLevel) { this.previousPermissionLevel = previousPermissionLevel; }

    public PermissionLevel getTargetPermissionLevel() { return targetPermissionLevel; }
    public void setTargetPermissionLevel(PermissionLevel targetPermissionLevel) { this.targetPermissionLevel = targetPermissionLevel; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public String getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(String initiatedBy) { this.initiatedBy = initiatedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public LocalDateTime getRolledBackAt() { return rolledBackAt; }
    public void setRolledBackAt(LocalDateTime rolledBackAt) { this.rolledBackAt = rolledBackAt; }

    public Boolean getStakeholderValidated() { return stakeholderValidated; }
    public void setStakeholderValidated(Boolean stakeholderValidated) { this.stakeholderValidated = stakeholderValidated; }

    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }

    public String getValidationNotes() { return validationNotes; }
    public void setValidationNotes(String validationNotes) { this.validationNotes = validationNotes; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
}
