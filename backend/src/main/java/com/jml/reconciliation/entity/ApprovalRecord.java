package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.ApprovalStatus;
import com.jml.reconciliation.model.enums.PermissionLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_records")
public class ApprovalRecord {

    @Id
    @Column(name = "approval_id", length = 50)
    private String approvalId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "application_name", nullable = false, length = 50)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false, length = 20)
    private PermissionLevel permissionLevel;

    @Column(name = "requested_by", nullable = false, length = 50)
    private String requestedBy;

    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "approval_date", nullable = false)
    private LocalDateTime approvalDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status;

    @Column(columnDefinition = "TEXT")
    private String reason;

    public ApprovalRecord() {}

    public ApprovalRecord(String approvalId, String userId, String applicationName, PermissionLevel permissionLevel, 
                          String requestedBy, String approvedBy, LocalDateTime approvalDate, ApprovalStatus status, String reason) {
        this.approvalId = approvalId;
        this.userId = userId;
        this.applicationName = applicationName;
        this.permissionLevel = permissionLevel;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.approvalDate = approvalDate;
        this.status = status;
        this.reason = reason;
    }

    public String getApprovalId() { return approvalId; }
    public void setApprovalId(String approvalId) { this.approvalId = approvalId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
