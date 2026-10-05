package com.jml.reconciliation.service;

import com.jml.reconciliation.dto.HealthOverrideRequest;
import com.jml.reconciliation.entity.DataSourceHealth;
import com.jml.reconciliation.model.enums.HealthState;
import com.jml.reconciliation.repository.DataSourceHealthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataSourceHealthService {

    private final DataSourceHealthRepository healthRepository;
    private final AuditService auditService;

    public DataSourceHealthService(DataSourceHealthRepository healthRepository, AuditService auditService) {
        this.healthRepository = healthRepository;
        this.auditService = auditService;
    }

    public List<DataSourceHealth> getAllHealth() {
        return healthRepository.findAll();
    }

    @Transactional
    public DataSourceHealth updateHealth(String sourceName, HealthOverrideRequest request) {
        DataSourceHealth health = healthRepository.findBySourceName(sourceName)
                .orElse(new DataSourceHealth(sourceName, HealthState.AVAILABLE, 25, 0.0));

        health.setStatus(request.getStatus());
        if (request.getLatencyMs() != null) health.setLatencyMs(request.getLatencyMs());
        if (request.getStalenessHours() != null) health.setDataStalenessHours(request.getStalenessHours());
        health.setLastSyncTime(LocalDateTime.now());

        DataSourceHealth saved = healthRepository.save(health);

        auditService.logEvent("DATA_SOURCE_HEALTH_UPDATED", "ADMIN", sourceName,
                String.format("Data feed %s status updated to %s (Latency: %dms, Staleness: %.1f hrs).",
                        sourceName, request.getStatus(), health.getLatencyMs(), health.getDataStalenessHours()));

        return saved;
    }
}
