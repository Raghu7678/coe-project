package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.RollbackRequest;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.service.RemediationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remediations")
public class RemediationController {

    private final RemediationService remediationService;

    public RemediationController(RemediationService remediationService) {
        this.remediationService = remediationService;
    }

    @GetMapping
    public List<RemediationAction> getAllRemediations() {
        return remediationService.getAllRemediations();
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<RemediationAction> executeRemediation(@PathVariable String id, 
                                                                 @RequestParam(defaultValue = "sec_admin") String actor) {
        RemediationAction executed = remediationService.executeRemediation(id, actor);
        return ResponseEntity.ok(executed);
    }

    @PostMapping("/{id}/rollback")
    public ResponseEntity<RemediationAction> rollbackRemediation(@PathVariable String id, 
                                                                 @Valid @RequestBody RollbackRequest request) {
        RemediationAction rolledBack = remediationService.rollbackRemediation(id, request.getActor(), request.getReason());
        return ResponseEntity.ok(rolledBack);
    }
}
