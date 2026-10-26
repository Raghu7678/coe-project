package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.ReconciliationIssue;
import com.jml.reconciliation.repository.ReconciliationIssueRepository;
import com.jml.reconciliation.service.BaselineEngineService;
import com.jml.reconciliation.service.ReconciliationEngineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * REST Controller for Access Risk Evaluation & Anomaly Inspection.
 * 
 * Exposes endpoints to:
 * - Trigger risk pre-qualification runs using either the PROTOTYPE or BASELINE evaluation engine.
 * - Retrieve all detected risk anomalies filtered by engine.
 * - Inspect granular multi-source evidence details for a specific detected anomaly.
 */
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

    /**
     * Triggers an on-demand access reconciliation / risk pre-qualification run.
     * 
     * @param engine evaluation engine to invoke: 'PROTOTYPE' (multi-source + confidence + safety gates)
     *               or 'BASELINE' (naive 2-source check)
     * @return List of detected ReconciliationIssue items
     */
    @PostMapping("/run")
    public List<ReconciliationIssue> runReconciliation(@RequestParam(defaultValue = "PROTOTYPE") String engine) {
        if ("BASELINE".equalsIgnoreCase(engine)) {
            return baselineEngineService.runBaselineReconciliation();
        }
        return prototypeEngineService.runReconciliation();
    }

    /**
     * Retrieves all detected access issues for a given engine from the database.
     * 
     * @param engine 'PROTOTYPE' or 'BASELINE' (case-insensitive, default: 'PROTOTYPE')
     * @return List of ReconciliationIssue entities
     */
    @GetMapping("/issues")
    public List<ReconciliationIssue> getIssues(@RequestParam(defaultValue = "PROTOTYPE") String engine) {
        return issueRepository.findByDetectionEngine(engine.toUpperCase());
    }

    /**
     * Inspects a specific detected risk anomaly by its database ID, including
     * multi-source evidence details, data confidence score, and safety gate status.
     * 
     * @param id primary key of the ReconciliationIssue
     * @return ReconciliationIssue entity
     * @throws NoSuchElementException if no issue exists with the given ID
     */
    @GetMapping("/issues/{id}")
    public ReconciliationIssue getIssueById(@PathVariable Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reconciliation issue not found with ID: " + id));
    }
}
