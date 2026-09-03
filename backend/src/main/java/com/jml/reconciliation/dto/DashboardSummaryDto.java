package com.jml.reconciliation.dto;

import com.jml.reconciliation.entity.DataSourceHealth;
import java.util.List;

public class DashboardSummaryDto {

    private long totalUsers;
    private long activeIssues;
    private long criticalIssues;
    private long orphanedAccessCount;
    private long excessiveAccessCount;
    private long unapprovedAccessCount;
    private long missingAccessCount;
    private long pendingApprovalsCount;
    private double targetCompliancePercentage;
    private double avgRemediationTimeMinutes;
    private List<DataSourceHealth> dataSources;

    public DashboardSummaryDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getActiveIssues() { return activeIssues; }
    public void setActiveIssues(long activeIssues) { this.activeIssues = activeIssues; }

    public long getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(long criticalIssues) { this.criticalIssues = criticalIssues; }

    public long getOrphanedAccessCount() { return orphanedAccessCount; }
    public void setOrphanedAccessCount(long orphanedAccessCount) { this.orphanedAccessCount = orphanedAccessCount; }

    public long getExcessiveAccessCount() { return excessiveAccessCount; }
    public void setExcessiveAccessCount(long excessiveAccessCount) { this.excessiveAccessCount = excessiveAccessCount; }

    public long getUnapprovedAccessCount() { return unapprovedAccessCount; }
    public void setUnapprovedAccessCount(long unapprovedAccessCount) { this.unapprovedAccessCount = unapprovedAccessCount; }

    public long getMissingAccessCount() { return missingAccessCount; }
    public void setMissingAccessCount(long missingAccessCount) { this.missingAccessCount = missingAccessCount; }

    public long getPendingApprovalsCount() { return pendingApprovalsCount; }
    public void setPendingApprovalsCount(long pendingApprovalsCount) { this.pendingApprovalsCount = pendingApprovalsCount; }

    public double getTargetCompliancePercentage() { return targetCompliancePercentage; }
    public void setTargetCompliancePercentage(double targetCompliancePercentage) { this.targetCompliancePercentage = targetCompliancePercentage; }

    public double getAvgRemediationTimeMinutes() { return avgRemediationTimeMinutes; }
    public void setAvgRemediationTimeMinutes(double avgRemediationTimeMinutes) { this.avgRemediationTimeMinutes = avgRemediationTimeMinutes; }

    public List<DataSourceHealth> getDataSources() { return dataSources; }
    public void setDataSources(List<DataSourceHealth> dataSources) { this.dataSources = dataSources; }
}
