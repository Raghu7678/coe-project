package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.RemediationActionType;
import com.jml.reconciliation.model.enums.RemediationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "remediation_actions")
public class RemediationAction {

    @Id
    @Column(name = "remediation_id", length = 50)
    private String remediationId;

    @Column(name = "issue_id", nullable = false, length = 50)
    private String issueId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "application_name", nullable = false, length = 50)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private RemediationActionType actionType;

    @Column(name = "previous_state", nullable = false, length = 100)
    private String previousState;

    @Column(name = "proposed_state", nullable = false, length = 100)
    private String proposedState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RemediationStatus status;

    @Column(name = "requested_by", nullable = false, length = 50)
    private String requestedBy;

    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "reviewer_comment", columnDefinition = "TEXT")
    private String reviewerComment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "rolled_back_at")
    private LocalDateTime rolledBackAt;

    @Column(name = "rollback_reason", columnDefinition = "TEXT")
    private String rollbackReason;

    public RemediationAction() {}

    public String getRemediationId() { return remediationId; }
    public void setRemediationId(String remediationId) { this.remediationId = remediationId; }

    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public RemediationActionType getActionType() { return actionType; }
    public void setActionType(RemediationActionType actionType) { this.actionType = actionType; }

    public String getPreviousState() { return previousState; }
    public void setPreviousState(String previousState) { this.previousState = previousState; }

    public String getProposedState() { return proposedState; }
    public void setProposedState(String proposedState) { this.proposedState = proposedState; }

    public RemediationStatus getStatus() { return status; }
    public void setStatus(RemediationStatus status) { this.status = status; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public LocalDateTime getRolledBackAt() { return rolledBackAt; }
    public void setRolledBackAt(LocalDateTime rolledBackAt) { this.rolledBackAt = rolledBackAt; }

    public String getRollbackReason() { return rollbackReason; }
    public void setRollbackReason(String rollbackReason) { this.rollbackReason = rollbackReason; }
}
