package com.jml.reconciliation.dto;

import com.jml.reconciliation.model.enums.HealthState;
import jakarta.validation.constraints.NotNull;

public class DataSourceHealthUpdateRequest {

    @NotNull(message = "Status is required")
    private HealthState status;

    private String freshness;

    public DataSourceHealthUpdateRequest() {}

    public DataSourceHealthUpdateRequest(HealthState status, String freshness) {
        this.status = status;
        this.freshness = freshness;
    }

    public HealthState getStatus() { return status; }
    public void setStatus(HealthState status) { this.status = status; }

    public String getFreshness() { return freshness; }
    public void setFreshness(String freshness) { this.freshness = freshness; }
}
