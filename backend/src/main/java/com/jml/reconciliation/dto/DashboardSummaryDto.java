package com.jml.reconciliation.dto;

import java.util.Map;

public class DashboardSummaryDto {

    private long totalEmployees;
    private long activeEmployees;
    private long leaversCount;
    private long highRiskIssuesCount;
    private long pendingApprovalsCount;
    private long executedRemediationsCount;
    private double averageDataConfidence;
    private Map<String, String> dataSourceHealthMap;

    public DashboardSummaryDto() {}

    public DashboardSummaryDto(long totalEmployees, long activeEmployees, long leaversCount, long highRiskIssuesCount, long pendingApprovalsCount, long executedRemediationsCount, double averageDataConfidence, Map<String, String> dataSourceHealthMap) {
        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.leaversCount = leaversCount;
        this.highRiskIssuesCount = highRiskIssuesCount;
        this.pendingApprovalsCount = pendingApprovalsCount;
        this.executedRemediationsCount = executedRemediationsCount;
        this.averageDataConfidence = averageDataConfidence;
        this.dataSourceHealthMap = dataSourceHealthMap;
    }

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public long getActiveEmployees() { return activeEmployees; }
    public void setActiveEmployees(long activeEmployees) { this.activeEmployees = activeEmployees; }

    public long getLeaversCount() { return leaversCount; }
    public void setLeaversCount(long leaversCount) { this.leaversCount = leaversCount; }

    public long getHighRiskIssuesCount() { return highRiskIssuesCount; }
    public void setHighRiskIssuesCount(long highRiskIssuesCount) { this.highRiskIssuesCount = highRiskIssuesCount; }

    public long getPendingApprovalsCount() { return pendingApprovalsCount; }
    public void setPendingApprovalsCount(long pendingApprovalsCount) { this.pendingApprovalsCount = pendingApprovalsCount; }

    public long getExecutedRemediationsCount() { return executedRemediationsCount; }
    public void setExecutedRemediationsCount(long executedRemediationsCount) { this.executedRemediationsCount = executedRemediationsCount; }

    public double getAverageDataConfidence() { return averageDataConfidence; }
    public void setAverageDataConfidence(double averageDataConfidence) { this.averageDataConfidence = averageDataConfidence; }

    public Map<String, String> getDataSourceHealthMap() { return dataSourceHealthMap; }
    public void setDataSourceHealthMap(Map<String, String> dataSourceHealthMap) { this.dataSourceHealthMap = dataSourceHealthMap; }
}
