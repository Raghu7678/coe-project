package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.DataSourceHealth;
import com.jml.reconciliation.model.enums.EventType;
import com.jml.reconciliation.model.enums.HealthState;
import com.jml.reconciliation.repository.DataSourceHealthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DataSourceHealthService {

    private final DataSourceHealthRepository healthRepository;
    private final AuditService auditService;

    public DataSourceHealthService(DataSourceHealthRepository healthRepository, AuditService auditService) {
        this.healthRepository = healthRepository;
        this.auditService = auditService;
    }

    public List<DataSourceHealth> getAllHealthStates() {
        return healthRepository.findAll();
    }

    public Optional<DataSourceHealth> getHealthState(String sourceName) {
        return healthRepository.findById(sourceName);
    }

    @Transactional
    public DataSourceHealth updateHealthState(String sourceName, HealthState status, String freshness) {
        DataSourceHealth health = healthRepository.findById(sourceName)
                .orElse(new DataSourceHealth(sourceName, status, LocalDateTime.now(), freshness, 0));

        HealthState oldStatus = health.getStatus();
        health.setStatus(status);
        if (freshness != null && !freshness.isEmpty()) {
            health.setFreshness(freshness);
        }
        health.setLastUpdated(LocalDateTime.now());

        DataSourceHealth saved = healthRepository.save(health);

        auditService.logEvent(
                EventType.DATA_SOURCE_HEALTH_CHANGED,
                "SYSTEM",
                "ADMIN",
                "UPDATE_HEALTH_STATE",
                "Status: " + oldStatus,
                "Status: " + status + ", Freshness: " + saved.getFreshness(),
                "Data source status changed via health management panel",
                null, null, sourceName
        );

        return saved;
    }

    public double calculateOverallConfidenceMultiplier() {
        List<DataSourceHealth> sources = healthRepository.findAll();
        if (sources.isEmpty()) return 1.0;

        double totalScore = 0.0;
        for (DataSourceHealth source : sources) {
            double score = 1.0;
            if (source.getStatus() == HealthState.STALE) score = 0.7;
            else if (source.getStatus() == HealthState.DELAYED) score = 0.5;
            else if (source.getStatus() == HealthState.UNAVAILABLE) score = 0.2;
            totalScore += score;
        }

        return Math.max(0.1, totalScore / sources.size());
    }
}
