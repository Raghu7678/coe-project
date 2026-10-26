package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.*;
import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Naive Baseline Access Evaluation Engine.
 * 
 * Serves as the comparative benchmark against the improved prototype engine.
 * 
 * Methodology & Flaws of the Baseline Approach:
 * 1. Single-attribute check: Evaluates ONLY HR employment status (LEFT) against application entitlements.
 * 2. Lacks Active Directory integration: Completely misses orphaned directory group memberships.
 * 3. Lacks Role Mover logic: Incapable of detecting privilege creep when employees switch departments.
 * 4. Lacks Approval History validation: Incapable of identifying unapproved administrative grants.
 * 5. Ignores Feed Freshness: Operates with naive 100% confidence assumption even when data sources are degraded.
 * 6. Zero Governance: Does not trigger safety gates or dual-control reviews, leading to unverified revocations.
 */
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

    /**
     * Executes the naive baseline evaluation against the dataset.
     * 
     * @return List of baseline-detected ReconciliationIssue items
     */
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
