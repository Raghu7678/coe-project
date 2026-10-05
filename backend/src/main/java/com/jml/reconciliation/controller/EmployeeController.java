package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.DashboardSummaryDto;
import com.jml.reconciliation.entity.DataSourceHealth;
import com.jml.reconciliation.entity.Employee;
import com.jml.reconciliation.model.enums.EmploymentStatus;
import com.jml.reconciliation.model.enums.RemediationStatus;
import com.jml.reconciliation.model.enums.RiskLevel;
import com.jml.reconciliation.repository.EmployeeRepository;
import com.jml.reconciliation.repository.ReconciliationIssueRepository;
import com.jml.reconciliation.repository.RemediationActionRepository;
import com.jml.reconciliation.service.DataSourceHealthService;
import com.jml.reconciliation.service.ReconciliationEngineService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final ReconciliationIssueRepository issueRepository;
    private final RemediationActionRepository actionRepository;
    private final ReconciliationEngineService reconciliationEngineService;
    private final DataSourceHealthService healthService;

    public EmployeeController(
            EmployeeRepository employeeRepository,
            ReconciliationIssueRepository issueRepository,
            RemediationActionRepository actionRepository,
            ReconciliationEngineService reconciliationEngineService,
            DataSourceHealthService healthService) {
        this.employeeRepository = employeeRepository;
        this.issueRepository = issueRepository;
        this.actionRepository = actionRepository;
        this.reconciliationEngineService = reconciliationEngineService;
        this.healthService = healthService;
    }

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/dashboard/summary")
    public DashboardSummaryDto getDashboardSummary() {
        long total = employeeRepository.count();
        long active = employeeRepository.findByStatus(EmploymentStatus.ACTIVE).size();
        long leavers = employeeRepository.findByStatus(EmploymentStatus.LEFT).size() + employeeRepository.findByStatus(EmploymentStatus.LEAVER).size();
        long highRiskCount = issueRepository.findByDetectionEngineAndRiskLevel("PROTOTYPE", RiskLevel.HIGH).size() +
                issueRepository.findByDetectionEngineAndRiskLevel("PROTOTYPE", RiskLevel.CRITICAL).size();
        long pendingApprovals = actionRepository.findByStatus(RemediationStatus.PENDING_APPROVAL).size();
        long executedRemediations = actionRepository.findByStatus(RemediationStatus.EXECUTED).size();
        double avgConfidence = reconciliationEngineService.calculateDataConfidence();

        Map<String, String> healthMap = new HashMap<>();
        for (DataSourceHealth h : healthService.getAllHealth()) {
            healthMap.put(h.getSourceName(), h.getStatus().name());
        }

        return new DashboardSummaryDto(
                total, active, leavers, highRiskCount, pendingApprovals, executedRemediations, avgConfidence, healthMap
        );
    }
}
