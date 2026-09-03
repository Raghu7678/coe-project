package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.*;
import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReconciliationEngineService {

    private final EmployeeRepository employeeRepository;
    private final RolePolicyRepository rolePolicyRepository;
    private final DirectoryGroupRepository directoryGroupRepository;
    private final EntitlementRepository entitlementRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final ReconciliationIssueRepository issueRepository;
    private final RemediationActionRepository remediationRepository;
    private final DataSourceHealthService healthService;
    private final AuditService auditService;

    public ReconciliationEngineService(EmployeeRepository employeeRepository,
                                       RolePolicyRepository rolePolicyRepository,
                                       DirectoryGroupRepository directoryGroupRepository,
                                       EntitlementRepository entitlementRepository,
                                       ApprovalRecordRepository approvalRecordRepository,
                                       ReconciliationIssueRepository issueRepository,
                                       RemediationActionRepository remediationRepository,
                                       DataSourceHealthService healthService,
                                       AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.rolePolicyRepository = rolePolicyRepository;
        this.directoryGroupRepository = directoryGroupRepository;
        this.entitlementRepository = entitlementRepository;
        this.approvalRecordRepository = approvalRecordRepository;
        this.issueRepository = issueRepository;
        this.remediationRepository = remediationRepository;
        this.healthService = healthService;
        this.auditService = auditService;
    }

    @Transactional
    public List<ReconciliationIssue> runReconciliation() {
        auditService.logEvent(
                EventType.RECONCILIATION_STARTED,
                "SYSTEM",
                "ENGINE",
                "RUN_PROTOTYPE_RECONCILIATION",
                null, null,
                "Initiated full multi-source access reconciliation run",
                null, null, "HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY"
        );

        // Delete previous prototype issues
        issueRepository.deleteByEngineType(EngineType.PROTOTYPE);

        List<ReconciliationIssue> detectedIssues = new ArrayList<>();
        List<Employee> employees = employeeRepository.findAll();

        // Check health of data sources
        Map<String, DataSourceHealth> healthMap = new HashMap<>();
        healthService.getAllHealthStates().forEach(h -> healthMap.put(h.getSourceName(), h));

        for (Employee employee : employees) {
            String userId = employee.getUserId();
            String currentRole = employee.getCurrentRole();
            String previousRole = employee.getPreviousRole();
            EmploymentStatus status = employee.getEmploymentStatus();

            List<RolePolicy> expectedPolicies = rolePolicyRepository.findByRoleName(currentRole);
            List<RolePolicy> previousRolePolicies = (previousRole != null) ? rolePolicyRepository.findByRoleName(previousRole) : Collections.emptyList();
            
            // Retrieve data if sources available
            List<Entitlement> entitlements = isSourceAvailable(healthMap, "APPLICATION_ENTITLEMENTS") 
                    ? entitlementRepository.findByUserId(userId) : Collections.emptyList();
            
            List<DirectoryGroup> directoryGroups = isSourceAvailable(healthMap, "DIRECTORY") 
                    ? directoryGroupRepository.findByUserId(userId) : Collections.emptyList();
            
            List<ApprovalRecord> approvals = isSourceAvailable(healthMap, "APPROVAL_HISTORY") 
                    ? approvalRecordRepository.findByUserId(userId) : Collections.emptyList();

            double userConfidence = calculateConfidenceScore(healthMap, !entitlements.isEmpty(), !directoryGroups.isEmpty(), !approvals.isEmpty());

            Set<String> processedApps = new HashSet<>();

            // 1. Check Application Entitlements
            for (Entitlement ent : entitlements) {
                String app = ent.getApplicationName();
                PermissionLevel actualLevel = ent.getPermissionLevel();
                processedApps.add(app);

                // A. Orphaned Access Check (Leaver)
                if (status == EmploymentStatus.LEFT) {
                    RiskLevel risk = (actualLevel == PermissionLevel.ADMIN) ? RiskLevel.CRITICAL : RiskLevel.HIGH;
                    ReconciliationIssue issue = createIssue(
                            employee, app, PermissionLevel.NONE, actualLevel,
                            IssueType.ORPHANED_ACCESS, risk, userConfidence,
                            RemediationActionType.REMOVE_ACCESS, 15
                    );
                    detectedIssues.add(issue);
                    continue;
                }

                // Find expected permission for current role
                Optional<RolePolicy> policyOpt = expectedPolicies.stream()
                        .filter(p -> p.getApplicationName().equalsIgnoreCase(app))
                        .findFirst();

                PermissionLevel expectedLevel = policyOpt.map(RolePolicy::getExpectedPermission).orElse(PermissionLevel.NONE);

                // B. Excessive Access Check
                if (actualLevel.isHigherThan(expectedLevel)) {
                    RiskLevel risk = (actualLevel == PermissionLevel.ADMIN) ? RiskLevel.HIGH : RiskLevel.MEDIUM;
                    int targetTime = (risk == RiskLevel.HIGH) ? 60 : 1440;
                    RemediationActionType action = (expectedLevel == PermissionLevel.NONE) ? RemediationActionType.REMOVE_ACCESS : RemediationActionType.REDUCE_PERMISSION;

                    ReconciliationIssue issue = createIssue(
                            employee, app, expectedLevel, actualLevel,
                            IssueType.EXCESSIVE_ACCESS, risk, userConfidence,
                            action, targetTime
                    );
                    detectedIssues.add(issue);
                }

                // C. Role Change Access Conflict Check
                if (previousRole != null && !previousRole.equals(currentRole)) {
                    boolean wasInOldRole = previousRolePolicies.stream().anyMatch(p -> p.getApplicationName().equalsIgnoreCase(app));
                    boolean isInNewRole = expectedPolicies.stream().anyMatch(p -> p.getApplicationName().equalsIgnoreCase(app));
                    if (wasInOldRole && !isInNewRole) {
                        ReconciliationIssue issue = createIssue(
                                employee, app, PermissionLevel.NONE, actualLevel,
                                IssueType.ROLE_CHANGE_ACCESS_CONFLICT, RiskLevel.HIGH, userConfidence,
                                RemediationActionType.REMOVE_ACCESS, 60
                        );
                        detectedIssues.add(issue);
                    }
                }

                // D. Unapproved Access Check
                if (actualLevel == PermissionLevel.ADMIN || actualLevel == PermissionLevel.WRITE) {
                    boolean hasValidApproval = approvals.stream().anyMatch(a -> 
                            a.getApplicationName().equalsIgnoreCase(app) && 
                            a.getStatus() == ApprovalStatus.APPROVED && 
                            !a.getPermissionLevel().isLowerThan(actualLevel)
                    );
                    if (!hasValidApproval && isSourceAvailable(healthMap, "APPROVAL_HISTORY")) {
                        // Check if not already added as excessive/orphaned to avoid duplicate issue noise
                        boolean alreadyFlagged = detectedIssues.stream().anyMatch(i -> 
                                i.getUserId().equals(userId) && i.getApplicationName().equalsIgnoreCase(app) && 
                                (i.getIssueType() == IssueType.ORPHANED_ACCESS || i.getIssueType() == IssueType.EXCESSIVE_ACCESS)
                        );
                        if (!alreadyFlagged) {
                            ReconciliationIssue issue = createIssue(
                                    employee, app, expectedLevel, actualLevel,
                                    IssueType.UNAPPROVED_ACCESS, RiskLevel.HIGH, userConfidence,
                                    RemediationActionType.REMOVE_ACCESS, 60
                            );
                            detectedIssues.add(issue);
                        }
                    }
                }
            }

            // 2. Check Directory Groups (Multi-source augmentation)
            for (DirectoryGroup group : directoryGroups) {
                String app = group.getApplicationName();
                PermissionLevel groupLevel = group.getAccessLevel();

                if (status == EmploymentStatus.LEFT && !processedApps.contains(app)) {
                    ReconciliationIssue issue = createIssue(
                            employee, app, PermissionLevel.NONE, groupLevel,
                            IssueType.ORPHANED_ACCESS, RiskLevel.HIGH, userConfidence,
                            RemediationActionType.REMOVE_FROM_GROUP, 15
                    );
                    detectedIssues.add(issue);
                }
            }

            // 3. Check Missing Access (Active Users)
            if (status == EmploymentStatus.ACTIVE) {
                for (RolePolicy policy : expectedPolicies) {
                    String app = policy.getApplicationName();
                    if (!processedApps.contains(app) && policy.getExpectedPermission() != PermissionLevel.NONE) {
                        ReconciliationIssue issue = createIssue(
                                employee, app, policy.getExpectedPermission(), PermissionLevel.NONE,
                                IssueType.MISSING_ACCESS, RiskLevel.LOW, userConfidence,
                                RemediationActionType.ADD_ACCESS, 1440
                        );
                        detectedIssues.add(issue);
                    }
                }
            }
        }

        List<ReconciliationIssue> savedIssues = issueRepository.saveAll(detectedIssues);

        // Generate remediation actions for each issue
        for (ReconciliationIssue issue : savedIssues) {
            createRemediationActionForIssue(issue);
        }

        return savedIssues;
    }

    private boolean isSourceAvailable(Map<String, DataSourceHealth> healthMap, String sourceName) {
        DataSourceHealth health = healthMap.get(sourceName);
        return health == null || health.getStatus() != HealthState.UNAVAILABLE;
    }

    private double calculateConfidenceScore(Map<String, DataSourceHealth> healthMap, 
                                           boolean hasEntitlements, boolean hasDirectory, boolean hasApprovals) {
        double confidence = 1.0;

        for (Map.Entry<String, DataSourceHealth> entry : healthMap.entrySet()) {
            HealthState status = entry.getValue().getStatus();
            if (status == HealthState.STALE) confidence -= 0.10;
            else if (status == HealthState.DELAYED) confidence -= 0.20;
            else if (status == HealthState.UNAVAILABLE) confidence -= 0.30;
        }

        return Math.max(0.15, Math.round(confidence * 100.0) / 100.0);
    }

    private ReconciliationIssue createIssue(Employee employee, String app, PermissionLevel expected, 
                                             PermissionLevel actual, IssueType type, RiskLevel risk, 
                                             double confidence, RemediationActionType action, int targetMinutes) {
        ReconciliationIssue issue = new ReconciliationIssue();
        issue.setIssueId("JML-" + UUID.randomUUID().toString().substring(0, 8));
        issue.setUserId(employee.getUserId());
        issue.setEmployeeName(employee.getName());
        issue.setCurrentRole(employee.getCurrentRole());
        issue.setIssueType(type);
        issue.setApplicationName(app);
        issue.setExpectedAccess(expected);
        issue.setActualAccess(actual);
        issue.setRiskLevel(risk);
        issue.setConfidenceScore(confidence);
        issue.setEngineType(EngineType.PROTOTYPE);
        issue.setRecommendedAction(action);
        issue.setStatus((risk == RiskLevel.HIGH || risk == RiskLevel.CRITICAL || confidence < 0.75) ? "PENDING_REVIEW" : "OPEN");
        issue.setDetectedAt(LocalDateTime.now());
        issue.setTargetTimeMinutes(targetMinutes);
        return issue;
    }

    private void createRemediationActionForIssue(ReconciliationIssue issue) {
        RemediationAction action = new RemediationAction();
        action.setRemediationId("REM-" + UUID.randomUUID().toString().substring(0, 8));
        action.setIssueId(issue.getIssueId());
        action.setUserId(issue.getUserId());
        action.setApplicationName(issue.getApplicationName());
        action.setActionType(issue.getRecommendedAction());
        action.setPreviousState("Permission: " + issue.getActualAccess());
        action.setProposedState("Permission: " + issue.getExpectedAccess());
        
        // Safety Gate: High Risk or Low Confidence requires accountable human review
        if (issue.getRiskLevel() == RiskLevel.HIGH || issue.getRiskLevel() == RiskLevel.CRITICAL || issue.getConfidenceScore() < 0.75) {
            action.setStatus(RemediationStatus.PENDING_REVIEW);
        } else {
            action.setStatus(RemediationStatus.RECOMMENDED);
        }

        action.setRequestedBy("JML_RECONCILIATION_ENGINE");
        action.setCreatedAt(LocalDateTime.now());
        remediationRepository.save(action);

        auditService.logEvent(
                EventType.ISSUE_DETECTED,
                issue.getUserId(),
                "ENGINE",
                "DETECT_ISSUE",
                "Actual: " + issue.getActualAccess(),
                "Expected: " + issue.getExpectedAccess(),
                "Detected " + issue.getIssueType() + " with risk " + issue.getRiskLevel() + " and confidence " + issue.getConfidenceScore(),
                issue.getIssueId(),
                null,
                "HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY"
        );
    }
}
