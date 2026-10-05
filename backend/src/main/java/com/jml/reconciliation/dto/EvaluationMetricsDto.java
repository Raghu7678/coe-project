package com.jml.reconciliation.dto;

public class EvaluationMetricsDto {

    private String engineName;
    private int groundTruthCount;
    private int detectedIssuesCount;
    private int truePositives;
    private int falsePositives;
    private int falseNegatives;
    private double precision;
    private double recall;
    private double detectionRate;
    private double targetSlaComplianceRate;

    public EvaluationMetricsDto() {}

    public EvaluationMetricsDto(String engineName, int groundTruthCount, int detectedIssuesCount, int truePositives, int falsePositives, int falseNegatives, double precision, double recall, double detectionRate, double targetSlaComplianceRate) {
        this.engineName = engineName;
        this.groundTruthCount = groundTruthCount;
        this.detectedIssuesCount = detectedIssuesCount;
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
        this.precision = precision;
        this.recall = recall;
        this.detectionRate = detectionRate;
        this.targetSlaComplianceRate = targetSlaComplianceRate;
    }

    public String getEngineName() { return engineName; }
    public void setEngineName(String engineName) { this.engineName = engineName; }

    public int getGroundTruthCount() { return groundTruthCount; }
    public void setGroundTruthCount(int groundTruthCount) { this.groundTruthCount = groundTruthCount; }

    public int getDetectedIssuesCount() { return detectedIssuesCount; }
    public void setDetectedIssuesCount(int detectedIssuesCount) { this.detectedIssuesCount = detectedIssuesCount; }

    public int getTruePositives() { return truePositives; }
    public void setTruePositives(int truePositives) { this.truePositives = truePositives; }

    public int getFalsePositives() { return falsePositives; }
    public void setFalsePositives(int falsePositives) { this.falsePositives = falsePositives; }

    public int getFalseNegatives() { return falseNegatives; }
    public void setFalseNegatives(int falseNegatives) { this.falseNegatives = falseNegatives; }

    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }

    public double getRecall() { return recall; }
    public void setRecall(double recall) { this.recall = recall; }

    public double getDetectionRate() { return detectionRate; }
    public void setDetectionRate(double detectionRate) { this.detectionRate = detectionRate; }

    public double getTargetSlaComplianceRate() { return targetSlaComplianceRate; }
    public void setTargetSlaComplianceRate(double targetSlaComplianceRate) { this.targetSlaComplianceRate = targetSlaComplianceRate; }
}
