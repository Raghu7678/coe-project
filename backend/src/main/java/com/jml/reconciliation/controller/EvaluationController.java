package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.EvaluationMetricsDto;
import com.jml.reconciliation.dto.ExperimentResultDto;
import com.jml.reconciliation.service.EvaluationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Defensible Empirical Evaluation & Benchmark Experiments.
 * 
 * Provides automated comparison between:
 * 1. The Naive Baseline Engine (simple 2-source HR vs App matching without safety gates or multi-feed confidence).
 * 2. The Improved Prototype Engine (4-feed multi-source ingestion with confidence degradation and dual-control safety gates).
 * 
 * Computes:
 * - Precision, Recall, F1 Score, and Ground Truth Detection Rates.
 * - Multi-trial Monte Carlo variance ($S^2$), Standard Error, and 95% Confidence Intervals.
 * - Target SLA compliance rate breakdown across Critical (15m), High (60m), and Standard (1440m) categories.
 */
@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin(originPatterns = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * Executes a single-run comparative experiment evaluating the Naive Baseline vs. Prototype Engine
     * against the pre-seeded ground truth dataset (N=12 known access issues).
     * 
     * @return List of EvaluationMetricsDto comparing Precision, Recall, and SLA Compliance
     */
    @GetMapping("/run")
    public List<EvaluationMetricsDto> runComparativeExperiment() {
        return evaluationService.runComparativeExperiment();
    }

    /**
     * Executes a defensible multi-trial experiment (default N=10 iterations) to assess statistical
     * stability, variance, 95% confidence intervals, and confidence-band error analysis.
     * 
     * @param trials number of experimental iterations (default: 10)
     * @return ExperimentResultDto with statistical aggregations, confidence intervals, and SLA performance
     */
    @GetMapping("/multi-run")
    public ExperimentResultDto runMultiTrialExperiment(@RequestParam(defaultValue = "10") int trials) {
        return evaluationService.runMultiTrialExperiment(trials);
    }
}
