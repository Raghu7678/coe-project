package com.jml.reconciliation.dto;

import com.jml.reconciliation.model.enums.HealthState;

public class HealthOverrideRequest {

    private HealthState status;
    private Integer latencyMs;
    private Double stalenessHours;

    public HealthOverrideRequest() {}

    public HealthOverrideRequest(HealthState status, Integer latencyMs, Double stalenessHours) {
        this.status = status;
        this.latencyMs = latencyMs;
        this.stalenessHours = stalenessHours;
    }

    public HealthState getStatus() { return status; }
    public void setStatus(HealthState status) { this.status = status; }

    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }

    public Double getStalenessHours() { return stalenessHours; }
    public void setStalenessHours(Double stalenessHours) { this.stalenessHours = stalenessHours; }
}
