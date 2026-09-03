package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.DataSourceHealthUpdateRequest;
import com.jml.reconciliation.entity.DataSourceHealth;
import com.jml.reconciliation.service.DataSourceHealthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-sources")
public class DataSourceHealthController {

    private final DataSourceHealthService healthService;

    public DataSourceHealthController(DataSourceHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public List<DataSourceHealth> getHealth() {
        return healthService.getAllHealthStates();
    }

    @PutMapping("/health/{sourceName}")
    public ResponseEntity<DataSourceHealth> updateHealth(@PathVariable String sourceName,
                                                          @Valid @RequestBody DataSourceHealthUpdateRequest request) {
        DataSourceHealth updated = healthService.updateHealthState(sourceName.toUpperCase(), request.getStatus(), request.getFreshness());
        return ResponseEntity.ok(updated);
    }
}
