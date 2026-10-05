package com.jml.reconciliation.dto;

public class ConfidenceErrorAnalysisDto {
    private String confidenceBand;
    private String rationale;
    private int issueCount;
    private int falsePositives;
    private int falseNegatives;
    private double precision;
    private String remediationStrategy;
    private String observedOutputDetails;

    public ConfidenceErrorAnalysisDto() {}

    public ConfidenceErrorAnalysisDto(String confidenceBand, String rationale, int issueCount, int falsePositives, int falseNegatives, double precision, String remediationStrategy, String observedOutputDetails) {
        this.confidenceBand = confidenceBand;
        this.rationale = rationale;
        this.issueCount = issueCount;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
        this.precision = precision;
        this.remediationStrategy = remediationStrategy;
        this.observedOutputDetails = observedOutputDetails;
    }

    public String getConfidenceBand() { return confidenceBand; }
    public void setConfidenceBand(String confidenceBand) { this.confidenceBand = confidenceBand; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public int getIssueCount() { return issueCount; }
    public void setIssueCount(int issueCount) { this.issueCount = issueCount; }

    public int getFalsePositives() { return falsePositives; }
    public void setFalsePositives(int falsePositives) { this.falsePositives = falsePositives; }

    public int getFalseNegatives() { return falseNegatives; }
    public void setFalseNegatives(int falseNegatives) { this.falseNegatives = falseNegatives; }

    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }

    public String getRemediationStrategy() { return remediationStrategy; }
    public void setRemediationStrategy(String remediationStrategy) { this.remediationStrategy = remediationStrategy; }

    public String getObservedOutputDetails() { return observedOutputDetails; }
    public void setObservedOutputDetails(String observedOutputDetails) { this.observedOutputDetails = observedOutputDetails; }
}
