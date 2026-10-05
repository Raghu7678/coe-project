package com.jml.reconciliation.dto;

public class SlaPerformanceDto {
    private String slaCategory;
    private int targetSlaMinutes;
    private double observedMeanMttrMinutes;
    private double varianceMttr;
    private double stdDevMttr;
    private double slaCompliancePercentage;
    private int totalIssuesEvaluated;
    private int slaBreachesCount;

    public SlaPerformanceDto() {}

    public SlaPerformanceDto(String slaCategory, int targetSlaMinutes, double observedMeanMttrMinutes, double varianceMttr, double stdDevMttr, double slaCompliancePercentage, int totalIssuesEvaluated, int slaBreachesCount) {
        this.slaCategory = slaCategory;
        this.targetSlaMinutes = targetSlaMinutes;
        this.observedMeanMttrMinutes = observedMeanMttrMinutes;
        this.varianceMttr = varianceMttr;
        this.stdDevMttr = stdDevMttr;
        this.slaCompliancePercentage = slaCompliancePercentage;
        this.totalIssuesEvaluated = totalIssuesEvaluated;
        this.slaBreachesCount = slaBreachesCount;
    }

    public String getSlaCategory() { return slaCategory; }
    public void setSlaCategory(String slaCategory) { this.slaCategory = slaCategory; }

    public int getTargetSlaMinutes() { return targetSlaMinutes; }
    public void setTargetSlaMinutes(int targetSlaMinutes) { this.targetSlaMinutes = targetSlaMinutes; }

    public double getObservedMeanMttrMinutes() { return observedMeanMttrMinutes; }
    public void setObservedMeanMttrMinutes(double observedMeanMttrMinutes) { this.observedMeanMttrMinutes = observedMeanMttrMinutes; }

    public double getVarianceMttr() { return varianceMttr; }
    public void setVarianceMttr(double varianceMttr) { this.varianceMttr = varianceMttr; }

    public double getStdDevMttr() { return stdDevMttr; }
    public void setStdDevMttr(double stdDevMttr) { this.stdDevMttr = stdDevMttr; }

    public double getSlaCompliancePercentage() { return slaCompliancePercentage; }
    public void setSlaCompliancePercentage(double slaCompliancePercentage) { this.slaCompliancePercentage = slaCompliancePercentage; }

    public int getTotalIssuesEvaluated() { return totalIssuesEvaluated; }
    public void setTotalIssuesEvaluated(int totalIssuesEvaluated) { this.totalIssuesEvaluated = totalIssuesEvaluated; }

    public int getSlaBreachesCount() { return slaBreachesCount; }
    public void setSlaBreachesCount(int slaBreachesCount) { this.slaBreachesCount = slaBreachesCount; }
}
