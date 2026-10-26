package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.HealthOverrideRequest;
import com.jml.reconciliation.entity.DataSourceHealth;
import com.jml.reconciliation.service.DataSourceHealthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Data Source Health & Resilience Testing.
 * 
 * Exposes endpoints to:
 * - Inspect the operational status, latency, and data staleness of all identity feeds (HR, Directory, Entitlements, Approvals).
 * - Simulate feed failure scenarios (e.g. marking a feed as STALE, DELAYED, or UNAVAILABLE)
 *   to empirically verify dynamic confidence score degradation and safety gate enforcement.
 */
@RestController
@RequestMapping("/api/data-sources")
@CrossOrigin(originPatterns = "*")
public class DataSourceHealthController {

    private final DataSourceHealthService healthService;

    public DataSourceHealthController(DataSourceHealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * Retrieves health metrics and statuses for all upstream data feeds.
     * 
     * @return List of DataSourceHealth records (HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY)
     */
    @GetMapping("/health")
    public List<DataSourceHealth> getAllHealth() {
        return healthService.getAllHealth();
    }

    /**
     * Simulates or overrides feed health for resilience testing.
     * 
     * Changing a feed to STALE (-0.15), DELAYED (-0.25), or UNAVAILABLE (-0.40) dynamically
     * degrades the composite confidence score, causing subsequent evaluation runs to force
     * low-confidence items into human dual-control review.
     * 
     * @param sourceName name of the data feed (e.g. 'HR', 'DIRECTORY')
     * @param request override parameters (status, latencyMs, stalenessHours)
     * @return updated DataSourceHealth entity
     */
    @PutMapping("/health/{sourceName}")
    public DataSourceHealth updateHealth(@PathVariable String sourceName, @RequestBody HealthOverrideRequest request) {
        return healthService.updateHealth(sourceName, request);
    }
}
