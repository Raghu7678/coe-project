package com.jml.reconciliation.dto;

import java.util.List;

public class EvaluationMetricsDto {

    private int groundTruthIssuesCount;

    // Baseline metrics
    private int baselineDetectedCount;
    private int baselineTruePositives;
    private int baselineFalsePositives;
    private int baselineFalseNegatives;
    private double baselinePrecision;
    private double baselineRecall;
    private double baselineDetectionRate;
    private double baselineTargetComplianceRate;

    // Prototype metrics
    private int prototypeDetectedCount;
    private int prototypeTruePositives;
    private int prototypeFalsePositives;
    private int prototypeFalseNegatives;
    private double prototypePrecision;
    private double prototypeRecall;
    private double prototypeDetectionRate;
    private double prototypeTargetComplianceRate;

    private List<String> errorAnalysisNotes;

    public EvaluationMetricsDto() {}

    public int getGroundTruthIssuesCount() { return groundTruthIssuesCount; }
    public void setGroundTruthIssuesCount(int groundTruthIssuesCount) { this.groundTruthIssuesCount = groundTruthIssuesCount; }

    public int getBaselineDetectedCount() { return baselineDetectedCount; }
    public void setBaselineDetectedCount(int baselineDetectedCount) { this.baselineDetectedCount = baselineDetectedCount; }

    public int getBaselineTruePositives() { return baselineTruePositives; }
    public void setBaselineTruePositives(int baselineTruePositives) { this.baselineTruePositives = baselineTruePositives; }

    public int getBaselineFalsePositives() { return baselineFalsePositives; }
    public void setBaselineFalsePositives(int baselineFalsePositives) { this.baselineFalsePositives = baselineFalsePositives; }

    public int getBaselineFalseNegatives() { return baselineFalseNegatives; }
    public void setBaselineFalseNegatives(int baselineFalseNegatives) { this.baselineFalseNegatives = baselineFalseNegatives; }

    public double getBaselinePrecision() { return baselinePrecision; }
    public void setBaselinePrecision(double baselinePrecision) { this.baselinePrecision = baselinePrecision; }

    public double getBaselineRecall() { return baselineRecall; }
    public void setBaselineRecall(double baselineRecall) { this.baselineRecall = baselineRecall; }

    public double getBaselineDetectionRate() { return baselineDetectionRate; }
    public void setBaselineDetectionRate(double baselineDetectionRate) { this.baselineDetectionRate = baselineDetectionRate; }

    public double getBaselineTargetComplianceRate() { return baselineTargetComplianceRate; }
    public void setBaselineTargetComplianceRate(double baselineTargetComplianceRate) { this.baselineTargetComplianceRate = baselineTargetComplianceRate; }

    public int getPrototypeDetectedCount() { return prototypeDetectedCount; }
    public void setPrototypeDetectedCount(int prototypeDetectedCount) { this.prototypeDetectedCount = prototypeDetectedCount; }

    public int getPrototypeTruePositives() { return prototypeTruePositives; }
    public void setPrototypeTruePositives(int prototypeTruePositives) { this.prototypeTruePositives = prototypeTruePositives; }

    public int getPrototypeFalsePositives() { return prototypeFalsePositives; }
    public void setPrototypeFalsePositives(int prototypeFalsePositives) { this.prototypeFalsePositives = prototypeFalsePositives; }

    public int getPrototypeFalseNegatives() { return prototypeFalseNegatives; }
    public void setPrototypeFalseNegatives(int prototypeFalseNegatives) { this.prototypeFalseNegatives = prototypeFalseNegatives; }

    public double getPrototypePrecision() { return prototypePrecision; }
    public void setPrototypePrecision(double prototypePrecision) { this.prototypePrecision = prototypePrecision; }

    public double getPrototypeRecall() { return prototypeRecall; }
    public void setPrototypeRecall(double prototypeRecall) { this.prototypeRecall = prototypeRecall; }

    public double getPrototypeDetectionRate() { return prototypeDetectionRate; }
    public void setPrototypeDetectionRate(double prototypeDetectionRate) { this.prototypeDetectionRate = prototypeDetectionRate; }

    public double getPrototypeTargetComplianceRate() { return prototypeTargetComplianceRate; }
    public void setPrototypeTargetComplianceRate(double prototypeTargetComplianceRate) { this.prototypeTargetComplianceRate = prototypeTargetComplianceRate; }

    public List<String> getErrorAnalysisNotes() { return errorAnalysisNotes; }
    public void setErrorAnalysisNotes(List<String> errorAnalysisNotes) { this.errorAnalysisNotes = errorAnalysisNotes; }
}
