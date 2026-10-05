package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.*;

import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BaselineEngineService {

    private final EmployeeRepository employeeRepository;
    private final EntitlementRepository entitlementRepository;
    private final ReconciliationIssueRepository issueRepository;
    private final AuditService auditService;

    public BaselineEngineService(
            EmployeeRepository employeeRepository,
            EntitlementRepository entitlementRepository,
            ReconciliationIssueRepository issueRepository,
            AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.entitlementRepository = entitlementRepository;
        this.issueRepository = issueRepository;
        this.auditService = auditService;
    }

    @Transactional
    public List<ReconciliationIssue> runBaselineReconciliation() {
        issueRepository.deleteByDetectionEngine("BASELINE");

        List<Employee> employees = employeeRepository.findAll();
        List<ReconciliationIssue> baselineIssues = new ArrayList<>();

        for (Employee emp : employees) {
            String username = emp.getUsername();
            List<Entitlement> entitlements = entitlementRepository.findByUsername(username);

            // Naive Baseline: Checks ONLY basic HR Employment Status vs Entitlement presence
            if (emp.getStatus() == EmploymentStatus.LEFT || emp.getStatus() == EmploymentStatus.LEAVER) {
                for (Entitlement ent : entitlements) {
                    ReconciliationIssue issue = new ReconciliationIssue(
                            username,
                            emp.getFullName(),
                            IssueType.ORPHANED_ACCESS_LEAVER,
                            RiskLevel.MEDIUM,
                            ent.getAppName(),
                            1.0, // Naive baseline assumes perfect confidence
                            "Naive 2-source check: Leaver retaining application entitlement.",
                            "SingleSourceCheck: HRStatus=" + emp.getStatus() + ", App=" + ent.getAppName(),
                            120,
                            "BASELINE",
                            false // No safety gate mechanism
                    );
                    baselineIssues.add(issueRepository.save(issue));
                }
            }
        }

        auditService.logEvent("BASELINE_RECONCILIATION_RUN", "SYSTEM", "BASELINE_ENGINE",
                String.format("Executed naive baseline check. Detected %d issues using 2-source evaluation.", baselineIssues.size()));

        return baselineIssues;
    }
}
