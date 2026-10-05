package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.EvaluationMetricsDto;
import com.jml.reconciliation.dto.ExperimentResultDto;
import com.jml.reconciliation.service.EvaluationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin(originPatterns = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/run")
    public List<EvaluationMetricsDto> runComparativeExperiment() {
        return evaluationService.runComparativeExperiment();
    }

    @GetMapping("/multi-run")
    public ExperimentResultDto runMultiTrialExperiment(@RequestParam(defaultValue = "10") int trials) {
        return evaluationService.runMultiTrialExperiment(trials);
    }
}
