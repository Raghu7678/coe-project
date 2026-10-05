package com.jml.reconciliation.dto;

import java.util.List;

public class ExperimentResultDto {
    private String engineName;
    private int totalTrials;
    private double meanPrecision;
    private double stdDevPrecision;
    private double stdErrPrecision;
    private double ci95PrecisionLower;
    private double ci95PrecisionUpper;

    private double meanRecall;
    private double stdDevRecall;
    private double stdErrRecall;
    private double ci95RecallLower;
    private double ci95RecallUpper;

    private double meanF1Score;
    private double stdDevF1Score;

    private double meanSlaComplianceRate;
    private double stdDevSlaComplianceRate;

    private List<SlaPerformanceDto> slaPerformances;
    private List<ConfidenceErrorAnalysisDto> errorAnalyses;

    public ExperimentResultDto() {}

    public String getEngineName() { return engineName; }
    public void setEngineName(String engineName) { this.engineName = engineName; }

    public int getTotalTrials() { return totalTrials; }
    public void setTotalTrials(int totalTrials) { this.totalTrials = totalTrials; }

    public double getMeanPrecision() { return meanPrecision; }
    public void setMeanPrecision(double meanPrecision) { this.meanPrecision = meanPrecision; }

    public double getStdDevPrecision() { return stdDevPrecision; }
    public void setStdDevPrecision(double stdDevPrecision) { this.stdDevPrecision = stdDevPrecision; }

    public double getStdErrPrecision() { return stdErrPrecision; }
    public void setStdErrPrecision(double stdErrPrecision) { this.stdErrPrecision = stdErrPrecision; }

    public double getCi95PrecisionLower() { return ci95PrecisionLower; }
    public void setCi95PrecisionLower(double ci95PrecisionLower) { this.ci95PrecisionLower = ci95PrecisionLower; }

    public double getCi95PrecisionUpper() { return ci95PrecisionUpper; }
    public void setCi95PrecisionUpper(double ci95PrecisionUpper) { this.ci95PrecisionUpper = ci95PrecisionUpper; }

    public double getMeanRecall() { return meanRecall; }
    public void setMeanRecall(double meanRecall) { this.meanRecall = meanRecall; }

    public double getStdDevRecall() { return stdDevRecall; }
    public void setStdDevRecall(double stdDevRecall) { this.stdDevRecall = stdDevRecall; }

    public double getStdErrRecall() { return stdErrRecall; }
    public void setStdErrRecall(double stdErrRecall) { this.stdErrRecall = stdErrRecall; }

    public double getCi95RecallLower() { return ci95RecallLower; }
    public void setCi95RecallLower(double ci95RecallLower) { this.ci95RecallLower = ci95RecallLower; }

    public double getCi95RecallUpper() { return ci95RecallUpper; }
    public void setCi95RecallUpper(double ci95RecallUpper) { this.ci95RecallUpper = ci95RecallUpper; }

    public double getMeanF1Score() { return meanF1Score; }
    public void setMeanF1Score(double meanF1Score) { this.meanF1Score = meanF1Score; }

    public double getStdDevF1Score() { return stdDevF1Score; }
    public void setStdDevF1Score(double stdDevF1Score) { this.stdDevF1Score = stdDevF1Score; }

    public double getMeanSlaComplianceRate() { return meanSlaComplianceRate; }
    public void setMeanSlaComplianceRate(double meanSlaComplianceRate) { this.meanSlaComplianceRate = meanSlaComplianceRate; }

    public double getStdDevSlaComplianceRate() { return stdDevSlaComplianceRate; }
    public void setStdDevSlaComplianceRate(double stdDevSlaComplianceRate) { this.stdDevSlaComplianceRate = stdDevSlaComplianceRate; }

    public List<SlaPerformanceDto> getSlaPerformances() { return slaPerformances; }
    public void setSlaPerformances(List<SlaPerformanceDto> slaPerformances) { this.slaPerformances = slaPerformances; }

    public List<ConfidenceErrorAnalysisDto> getErrorAnalyses() { return errorAnalyses; }
    public void setErrorAnalyses(List<ConfidenceErrorAnalysisDto> errorAnalyses) { this.errorAnalyses = errorAnalyses; }
}
