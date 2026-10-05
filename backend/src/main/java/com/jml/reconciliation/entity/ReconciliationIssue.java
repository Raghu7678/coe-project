package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.IssueType;
import com.jml.reconciliation.model.enums.RiskLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_issues")
public class ReconciliationIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType issueType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    private String appName;

    private Double dataConfidenceScore; // 0.0 - 1.0

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String evidenceDetails;

    private Integer slaTargetMinutes;

    private String detectionEngine; // PROTOTYPE or BASELINE

    private Boolean safetyGateTriggered; // Routed to PENDING_APPROVAL

    private LocalDateTime detectedAt;

    public ReconciliationIssue() {}

    public ReconciliationIssue(String username, String fullName, IssueType issueType, RiskLevel riskLevel, String appName, Double dataConfidenceScore, String description, String evidenceDetails, Integer slaTargetMinutes, String detectionEngine, Boolean safetyGateTriggered) {
        this.username = username;
        this.fullName = fullName;
        this.issueType = issueType;
        this.riskLevel = riskLevel;
        this.appName = appName;
        this.dataConfidenceScore = dataConfidenceScore;
        this.description = description;
        this.evidenceDetails = evidenceDetails;
        this.slaTargetMinutes = slaTargetMinutes;
        this.detectionEngine = detectionEngine;
        this.safetyGateTriggered = safetyGateTriggered;
        this.detectedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public IssueType getIssueType() { return issueType; }
    public void setIssueType(IssueType issueType) { this.issueType = issueType; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public Double getDataConfidenceScore() { return dataConfidenceScore; }
    public void setDataConfidenceScore(Double dataConfidenceScore) { this.dataConfidenceScore = dataConfidenceScore; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEvidenceDetails() { return evidenceDetails; }
    public void setEvidenceDetails(String evidenceDetails) { this.evidenceDetails = evidenceDetails; }

    public Integer getSlaTargetMinutes() { return slaTargetMinutes; }
    public void setSlaTargetMinutes(Integer slaTargetMinutes) { this.slaTargetMinutes = slaTargetMinutes; }

    public String getDetectionEngine() { return detectionEngine; }
    public void setDetectionEngine(String detectionEngine) { this.detectionEngine = detectionEngine; }

    public Boolean getSafetyGateTriggered() { return safetyGateTriggered; }
    public void setSafetyGateTriggered(Boolean safetyGateTriggered) { this.safetyGateTriggered = safetyGateTriggered; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
}
