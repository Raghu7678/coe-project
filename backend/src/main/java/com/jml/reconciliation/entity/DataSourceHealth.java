package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.HealthState;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_source_health")
public class DataSourceHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sourceName; // HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HealthState status;

    private Integer latencyMs;

    private Double dataStalenessHours;

    private LocalDateTime lastSyncTime;

    public DataSourceHealth() {}

    public DataSourceHealth(String sourceName, HealthState status, Integer latencyMs, Double dataStalenessHours) {
        this.sourceName = sourceName;
        this.status = status;
        this.latencyMs = latencyMs;
        this.dataStalenessHours = dataStalenessHours;
        this.lastSyncTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public HealthState getStatus() { return status; }
    public void setStatus(HealthState status) { this.status = status; }

    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }

    public Double getDataStalenessHours() { return dataStalenessHours; }
    public void setDataStalenessHours(Double dataStalenessHours) { this.dataStalenessHours = dataStalenessHours; }

    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
