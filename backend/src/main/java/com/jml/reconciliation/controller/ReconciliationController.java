package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.ReconciliationIssue;
import com.jml.reconciliation.repository.ReconciliationIssueRepository;
import com.jml.reconciliation.service.BaselineEngineService;
import com.jml.reconciliation.service.ReconciliationEngineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation")
@CrossOrigin(originPatterns = "*")
public class ReconciliationController {

    private final ReconciliationEngineService prototypeEngineService;
    private final BaselineEngineService baselineEngineService;
    private final ReconciliationIssueRepository issueRepository;

    public ReconciliationController(
            ReconciliationEngineService prototypeEngineService,
            BaselineEngineService baselineEngineService,
            ReconciliationIssueRepository issueRepository) {
        this.prototypeEngineService = prototypeEngineService;
        this.baselineEngineService = baselineEngineService;
        this.issueRepository = issueRepository;
    }

    @PostMapping("/run")
    public List<ReconciliationIssue> runReconciliation(@RequestParam(defaultValue = "PROTOTYPE") String engine) {
        if ("BASELINE".equalsIgnoreCase(engine)) {
            return baselineEngineService.runBaselineReconciliation();
        }
        return prototypeEngineService.runReconciliation();
    }

    @GetMapping("/issues")
    public List<ReconciliationIssue> getIssues(@RequestParam(defaultValue = "PROTOTYPE") String engine) {
        return issueRepository.findByDetectionEngine(engine.toUpperCase());
    }

    @GetMapping("/issues/{id}")
    public ReconciliationIssue getIssueById(@PathVariable Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found with ID: " + id));
    }
}
