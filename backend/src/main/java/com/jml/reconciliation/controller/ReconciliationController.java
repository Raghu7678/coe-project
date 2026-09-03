package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.ReconciliationIssue;
import com.jml.reconciliation.model.enums.EngineType;
import com.jml.reconciliation.repository.ReconciliationIssueRepository;
import com.jml.reconciliation.service.BaselineEngineService;
import com.jml.reconciliation.service.ReconciliationEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final ReconciliationEngineService prototypeEngineService;
    private final BaselineEngineService baselineEngineService;
    private final ReconciliationIssueRepository issueRepository;

    public ReconciliationController(ReconciliationEngineService prototypeEngineService,
                                    BaselineEngineService baselineEngineService,
                                    ReconciliationIssueRepository issueRepository) {
        this.prototypeEngineService = prototypeEngineService;
        this.baselineEngineService = baselineEngineService;
        this.issueRepository = issueRepository;
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runReconciliation(@RequestParam(defaultValue = "PROTOTYPE") String engine) {
        long startTime = System.currentTimeMillis();
        List<ReconciliationIssue> issues;

        if ("BASELINE".equalsIgnoreCase(engine)) {
            issues = baselineEngineService.runBaselineReconciliation();
        } else {
            issues = prototypeEngineService.runReconciliation();
        }

        long duration = System.currentTimeMillis() - startTime;

        return ResponseEntity.ok(Map.of(
                "engineType", engine,
                "issuesDetectedCount", issues.size(),
                "executionTimeMs", duration,
                "issues", issues
        ));
    }

    @GetMapping("/issues")
    public List<ReconciliationIssue> getIssues(@RequestParam(defaultValue = "PROTOTYPE") String engine) {
        EngineType engineType = "BASELINE".equalsIgnoreCase(engine) ? EngineType.BASELINE : EngineType.PROTOTYPE;
        return issueRepository.findByEngineType(engineType);
    }

    @GetMapping("/issues/{id}")
    public ResponseEntity<ReconciliationIssue> getIssueById(@PathVariable String id) {
        return issueRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
