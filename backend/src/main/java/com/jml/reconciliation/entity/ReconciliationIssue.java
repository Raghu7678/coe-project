package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.EngineType;
import com.jml.reconciliation.model.enums.IssueType;
import com.jml.reconciliation.model.enums.PermissionLevel;
import com.jml.reconciliation.model.enums.RemediationActionType;
import com.jml.reconciliation.model.enums.RiskLevel;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_issues")
public class ReconciliationIssue {

    @Id
    @Column(name = "issue_id", length = 50)
    private String issueId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "employee_name", nullable = false, length = 100)
    private String employeeName;

    @Column(name = "current_role", nullable = false, length = 50)
    private String currentRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_type", nullable = false, length = 50)
    private IssueType issueType;

    @Column(name = "application_name", nullable = false, length = 50)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "expected_access", length = 20)
    private PermissionLevel expectedAccess;

    @Enumerated(EnumType.STRING)
    @Column(name = "actual_access", length = 20)
    private PermissionLevel actualAccess;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(name = "confidence_score", nullable = false)
    private double confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false, length = 20)
    private EngineType engineType;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_action", nullable = false, length = 50)
    private RemediationActionType recommendedAction;

    @Column(nullable = false, length = 30)
    private String status; // OPEN, PENDING_REVIEW, RESOLVED, ROLLED_BACK

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "remediated_at")
    private LocalDateTime remediatedAt;

    @Column(name = "remediation_time_minutes")
    private Integer remediationTimeMinutes;

    @Column(name = "target_time_minutes")
    private Integer targetTimeMinutes;

    @Column(name = "met_target")
    private Boolean metTarget;

    public ReconciliationIssue() {}

    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    public IssueType getIssueType() { return issueType; }
    public void setIssueType(IssueType issueType) { this.issueType = issueType; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public PermissionLevel getExpectedAccess() { return expectedAccess; }
    public void setExpectedAccess(PermissionLevel expectedAccess) { this.expectedAccess = expectedAccess; }

    public PermissionLevel getActualAccess() { return actualAccess; }
    public void setActualAccess(PermissionLevel actualAccess) { this.actualAccess = actualAccess; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public EngineType getEngineType() { return engineType; }
    public void setEngineType(EngineType engineType) { this.engineType = engineType; }

    public RemediationActionType getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(RemediationActionType recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public LocalDateTime getRemediatedAt() { return remediatedAt; }
    public void setRemediatedAt(LocalDateTime remediatedAt) { this.remediatedAt = remediatedAt; }

    public Integer getRemediationTimeMinutes() { return remediationTimeMinutes; }
    public void setRemediationTimeMinutes(Integer remediationTimeMinutes) { this.remediationTimeMinutes = remediationTimeMinutes; }

    public Integer getTargetTimeMinutes() { return targetTimeMinutes; }
    public void setTargetTimeMinutes(Integer targetTimeMinutes) { this.targetTimeMinutes = targetTimeMinutes; }

    public Boolean getMetTarget() { return metTarget; }
    public void setMetTarget(Boolean metTarget) { this.metTarget = metTarget; }
}
