package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_records")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String approvalId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String appName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionLevel grantedPermission;

    private String requestedBy;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private String approvalStatus; // APPROVED, REJECTED, PENDING

    private String rationale;

    public ApprovalRecord() {}

    public ApprovalRecord(String approvalId, String username, String appName, PermissionLevel grantedPermission, String requestedBy, String approvedBy, LocalDateTime approvedAt, String approvalStatus, String rationale) {
        this.approvalId = approvalId;
        this.username = username;
        this.appName = appName;
        this.grantedPermission = grantedPermission;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.approvalStatus = approvalStatus;
        this.rationale = rationale;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApprovalId() { return approvalId; }
    public void setApprovalId(String approvalId) { this.approvalId = approvalId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public PermissionLevel getGrantedPermission() { return grantedPermission; }
    public void setGrantedPermission(PermissionLevel grantedPermission) { this.grantedPermission = grantedPermission; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
}
