package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.*;
import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BaselineEngineService {

    private final EmployeeRepository employeeRepository;
    private final RolePolicyRepository rolePolicyRepository;
    private final EntitlementRepository entitlementRepository;
    private final ReconciliationIssueRepository issueRepository;

    public BaselineEngineService(EmployeeRepository employeeRepository,
                                  RolePolicyRepository rolePolicyRepository,
                                  EntitlementRepository entitlementRepository,
                                  ReconciliationIssueRepository issueRepository) {
        this.employeeRepository = employeeRepository;
        this.rolePolicyRepository = rolePolicyRepository;
        this.entitlementRepository = entitlementRepository;
        this.issueRepository = issueRepository;
    }

    @Transactional
    public List<ReconciliationIssue> runBaselineReconciliation() {
        // Clear previous baseline issues
        issueRepository.deleteByEngineType(EngineType.BASELINE);

        List<ReconciliationIssue> detectedIssues = new ArrayList<>();
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            String userId = employee.getUserId();
            String currentRole = employee.getCurrentRole();
            EmploymentStatus status = employee.getEmploymentStatus();

            List<RolePolicy> expectedPolicies = rolePolicyRepository.findByRoleName(currentRole);
            List<Entitlement> actualEntitlements = entitlementRepository.findByUserId(userId);

            Set<String> processedApps = new HashSet<>();

            // 1. Check all actual entitlements against expected
            for (Entitlement ent : actualEntitlements) {
                String app = ent.getApplicationName();
                PermissionLevel actualLevel = ent.getPermissionLevel();
                processedApps.add(app);

                // Baseline Orphaned Access Check (Naive: status = LEFT & ent exists)
                if (status == EmploymentStatus.LEFT) {
                    ReconciliationIssue issue = createBaselineIssue(
                            employee, app, PermissionLevel.NONE, actualLevel,
                            IssueType.ORPHANED_ACCESS, RiskLevel.HIGH,
                            RemediationActionType.REMOVE_ACCESS
                    );
                    detectedIssues.add(issue);
                    continue;
                }

                // Find expected level
                Optional<RolePolicy> policyOpt = expectedPolicies.stream()
                        .filter(p -> p.getApplicationName().equalsIgnoreCase(app))
                        .findFirst();

                PermissionLevel expectedLevel = policyOpt.map(RolePolicy::getExpectedPermission).orElse(PermissionLevel.NONE);

                if (actualLevel.isHigherThan(expectedLevel)) {
                    ReconciliationIssue issue = createBaselineIssue(
                            employee, app, expectedLevel, actualLevel,
                            IssueType.EXCESSIVE_ACCESS, RiskLevel.MEDIUM,
                            RemediationActionType.REDUCE_PERMISSION
                    );
                    detectedIssues.add(issue);
                }
            }

            // 2. Check missing expected entitlements (if active)
            if (status == EmploymentStatus.ACTIVE) {
                for (RolePolicy policy : expectedPolicies) {
                    String app = policy.getApplicationName();
                    if (!processedApps.contains(app) && policy.getExpectedPermission() != PermissionLevel.NONE) {
                        ReconciliationIssue issue = createBaselineIssue(
                                employee, app, policy.getExpectedPermission(), PermissionLevel.NONE,
                                IssueType.MISSING_ACCESS, RiskLevel.LOW,
                                RemediationActionType.ADD_ACCESS
                        );
                        detectedIssues.add(issue);
                    }
                }
            }
        }

        return issueRepository.saveAll(detectedIssues);
    }

    private ReconciliationIssue createBaselineIssue(Employee employee, String app, PermissionLevel expected, 
                                                     PermissionLevel actual, IssueType type, RiskLevel risk, 
                                                     RemediationActionType action) {
        ReconciliationIssue issue = new ReconciliationIssue();
        issue.setIssueId("BASE-" + UUID.randomUUID().toString().substring(0, 8));
        issue.setUserId(employee.getUserId());
        issue.setEmployeeName(employee.getName());
        issue.setCurrentRole(employee.getCurrentRole());
        issue.setIssueType(type);
        issue.setApplicationName(app);
        issue.setExpectedAccess(expected);
        issue.setActualAccess(actual);
        issue.setRiskLevel(risk);
        issue.setConfidenceScore(0.5); // Fixed naive confidence score
        issue.setEngineType(EngineType.BASELINE);
        issue.setRecommendedAction(action);
        issue.setStatus("OPEN");
        issue.setDetectedAt(LocalDateTime.now());
        issue.setTargetTimeMinutes(1440); // Standard 24h default in baseline
        return issue;
    }
}
