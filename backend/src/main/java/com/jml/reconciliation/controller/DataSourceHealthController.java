package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.HealthOverrideRequest;
import com.jml.reconciliation.entity.DataSourceHealth;
import com.jml.reconciliation.service.DataSourceHealthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-sources")
@CrossOrigin(originPatterns = "*")
public class DataSourceHealthController {

    private final DataSourceHealthService healthService;

    public DataSourceHealthController(DataSourceHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public List<DataSourceHealth> getAllHealth() {
        return healthService.getAllHealth();
    }

    @PutMapping("/health/{sourceName}")
    public DataSourceHealth updateHealth(@PathVariable String sourceName, @RequestBody HealthOverrideRequest request) {
        return healthService.updateHealth(sourceName, request);
    }
}
