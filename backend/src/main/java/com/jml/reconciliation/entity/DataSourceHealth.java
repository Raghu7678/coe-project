package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.HealthState;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_source_health")
public class DataSourceHealth {

    @Id
    @Column(name = "source_name", length = 50)
    private String sourceName; // HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HealthState status;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false, length = 20)
    private String freshness; // FRESH, STALE, UNKNOWN

    @Column(name = "records_count")
    private Integer recordsCount;

    public DataSourceHealth() {}

    public DataSourceHealth(String sourceName, HealthState status, LocalDateTime lastUpdated, String freshness, Integer recordsCount) {
        this.sourceName = sourceName;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.freshness = freshness;
        this.recordsCount = recordsCount;
    }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public HealthState getStatus() { return status; }
    public void setStatus(HealthState status) { this.status = status; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getFreshness() { return freshness; }
    public void setFreshness(String freshness) { this.freshness = freshness; }

    public Integer getRecordsCount() { return recordsCount; }
    public void setRecordsCount(Integer recordsCount) { this.recordsCount = recordsCount; }
}
