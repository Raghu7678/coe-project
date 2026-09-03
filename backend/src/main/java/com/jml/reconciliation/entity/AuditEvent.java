package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.EventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @Column(name = "audit_id", length = 50)
    private String auditId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String actor;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "previous_state", columnDefinition = "TEXT")
    private String previousState;

    @Column(name = "new_state", columnDefinition = "TEXT")
    private String newState;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "related_issue_id", length = 50)
    private String relatedIssueId;

    @Column(name = "related_approval_id", length = 50)
    private String relatedApprovalId;

    @Column(name = "data_sources_used", length = 255)
    private String dataSourcesUsed;

    public AuditEvent() {}

    public AuditEvent(String auditId, LocalDateTime timestamp, EventType eventType, String userId, 
                      String actor, String action, String previousState, String newState, 
                      String reason, String relatedIssueId, String relatedApprovalId, String dataSourcesUsed) {
        this.auditId = auditId;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.userId = userId;
        this.actor = actor;
        this.action = action;
        this.previousState = previousState;
        this.newState = newState;
        this.reason = reason;
        this.relatedIssueId = relatedIssueId;
        this.relatedApprovalId = relatedApprovalId;
        this.dataSourcesUsed = dataSourcesUsed;
    }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPreviousState() { return previousState; }
    public void setPreviousState(String previousState) { this.previousState = previousState; }

    public String getNewState() { return newState; }
    public void setNewState(String newState) { this.newState = newState; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRelatedIssueId() { return relatedIssueId; }
    public void setRelatedIssueId(String relatedIssueId) { this.relatedIssueId = relatedIssueId; }

    public String getRelatedApprovalId() { return relatedApprovalId; }
    public void setRelatedApprovalId(String relatedApprovalId) { this.relatedApprovalId = relatedApprovalId; }

    public String getDataSourcesUsed() { return dataSourcesUsed; }
    public void setDataSourcesUsed(String dataSourcesUsed) { this.dataSourcesUsed = dataSourcesUsed; }
}
