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
import java.util.NoSuchElementException;

/**
 * REST Controller for Personnel Entities & Executive Dashboard Aggregations.
 * 
 * Provides:
 * - Query endpoints for all 40 pre-seeded identities (/api/employees, /api/contractors)
 * - Single identity lookup by username or employee code
 * - High-performance aggregate summary statistics for the executive cyber dashboard
 */
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

    /**
     * Lists all employee / contractor identities currently registered in the system.
     * Supports both /api/employees and /api/contractors aliases.
     * 
     * @return List of Employee records
     */
    @GetMapping({"/employees", "/contractors"})
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Retrieves an individual employee or contractor by username or employee ID.
     * 
     * @param identifier username (e.g. 'usr_alex') or employeeId (e.g. 'EMP-1001')
     * @return Employee entity
     */
    @GetMapping({"/employees/{identifier}", "/contractors/{identifier}"})
    public Employee getEmployeeByIdentifier(@PathVariable String identifier) {
        return employeeRepository.findByUsername(identifier)
                .or(() -> employeeRepository.findByEmployeeId(identifier))
                .orElseThrow(() -> new NoSuchElementException("Personnel identity not found with identifier: " + identifier));
    }

    /**
     * Compiles aggregate summary metrics for the executive dashboard.
     * 
     * Metrics calculated:
     * - Total identities count
     * - Active personnel count
     * - Terminated leavers retaining potential access
     * - High and Critical risk anomaly counts
     * - Pending dual-control approval actions
     * - Executed remediation actions
     * - Real-time average data confidence score (computed from source feeds)
     * - Current health status map across all 4 feeds
     * 
     * @return DashboardSummaryDto containing system-wide KPIs
     */
    @GetMapping("/dashboard/summary")
    public DashboardSummaryDto getDashboardSummary() {
        long total = employeeRepository.count();
        long active = employeeRepository.findByStatus(EmploymentStatus.ACTIVE).size();
        long leavers = employeeRepository.findByStatus(EmploymentStatus.LEFT).size() +
                employeeRepository.findByStatus(EmploymentStatus.LEAVER).size();
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
