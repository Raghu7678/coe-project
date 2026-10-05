package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.*;

import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReconciliationEngineService {

    private final EmployeeRepository employeeRepository;
    private final EntitlementRepository entitlementRepository;
    private final DirectoryGroupRepository directoryGroupRepository;
    private final ApprovalRecordRepository approvalRepository;
    private final RolePolicyRepository rolePolicyRepository;
    private final ReconciliationIssueRepository issueRepository;
    private final RemediationActionRepository actionRepository;
    private final DataSourceHealthRepository healthRepository;
    private final AuditService auditService;

    public ReconciliationEngineService(
            EmployeeRepository employeeRepository,
            EntitlementRepository entitlementRepository,
            DirectoryGroupRepository directoryGroupRepository,
            ApprovalRecordRepository approvalRepository,
            RolePolicyRepository rolePolicyRepository,
            ReconciliationIssueRepository issueRepository,
            RemediationActionRepository actionRepository,
            DataSourceHealthRepository healthRepository,
            AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.entitlementRepository = entitlementRepository;
        this.directoryGroupRepository = directoryGroupRepository;
        this.approvalRepository = approvalRepository;
        this.rolePolicyRepository = rolePolicyRepository;
        this.issueRepository = issueRepository;
        this.actionRepository = actionRepository;
        this.healthRepository = healthRepository;
        this.auditService = auditService;
    }

    @Transactional
    public List<ReconciliationIssue> runReconciliation() {
        issueRepository.deleteByDetectionEngine("PROTOTYPE");

        double confidenceScore = calculateDataConfidence();
        List<Employee> employees = employeeRepository.findAll();
        List<ReconciliationIssue> detectedIssues = new ArrayList<>();

        for (Employee emp : employees) {
            String username = emp.getUsername();
            List<Entitlement> entitlements = entitlementRepository.findByUsername(username);
            List<DirectoryGroup> groups = directoryGroupRepository.findByUsername(username);
            List<ApprovalRecord> approvals = approvalRepository.findByUsername(username);
            List<RolePolicy> currentRolePolicies = rolePolicyRepository.findByRoleName(emp.getCurrentRole());

            Map<String, PermissionLevel> policyMap = currentRolePolicies.stream()
                    .collect(Collectors.toMap(RolePolicy::getAppName, RolePolicy::getRequiredPermission, (a, b) -> a));

            // 1. Check Orphaned Access for Leavers
            if (emp.getStatus() == EmploymentStatus.LEFT || emp.getStatus() == EmploymentStatus.LEAVER) {
                for (Entitlement ent : entitlements) {
                    RiskLevel level = ent.getPermissionLevel() == PermissionLevel.ADMIN ? RiskLevel.CRITICAL : RiskLevel.HIGH;
                    boolean gate = level == RiskLevel.CRITICAL || confidenceScore < 0.75;

                    ReconciliationIssue issue = new ReconciliationIssue(
                            username,
                            emp.getFullName(),
                            IssueType.ORPHANED_ACCESS_LEAVER,
                            level,
                            ent.getAppName(),
                            confidenceScore,
                            String.format("Leaver retaining active entitlement '%s' with %s permission.", ent.getAppName(), ent.getPermissionLevel()),
                            String.format("HRStatus=%s, App=%s, EntitlementPermission=%s", emp.getStatus(), ent.getAppName(), ent.getPermissionLevel()),
                            level == RiskLevel.CRITICAL ? 15 : 60,
                            "PROTOTYPE",
                            gate
                    );
                    detectedIssues.add(saveAndGate(issue, emp, "REVOKE_ORPHANED_ACCESS", PermissionLevel.NONE));
                }

                for (DirectoryGroup grp : groups) {
                    ReconciliationIssue issue = new ReconciliationIssue(
                            username,
                            emp.getFullName(),
                            IssueType.ORPHANED_ACCESS_LEAVER,
                            RiskLevel.HIGH,
                            "Directory Group: " + grp.getGroupName(),
                            confidenceScore,
                            String.format("Leaver retaining directory group membership '%s'.", grp.getGroupName()),
                            String.format("HRStatus=%s, DirectoryGroup=%s", emp.getStatus(), grp.getGroupName()),
                            60,
                            "PROTOTYPE",
                            true
                    );
                    detectedIssues.add(saveAndGate(issue, emp, "REVOKE_DIRECTORY_GROUP", PermissionLevel.NONE));
                }
                continue;
            }

            // 2. Check Excessive Access for Role Movers
            if (emp.getStatus() == EmploymentStatus.ROLE_MOVER) {
                for (Entitlement ent : entitlements) {
                    PermissionLevel allowed = policyMap.get(ent.getAppName());
                    if (allowed == null || isHigherPermission(ent.getPermissionLevel(), allowed)) {
                        // Check if there is an explicit approval record for this role transition
                        boolean hasApprovedExemption = approvals.stream().anyMatch(a ->
                                a.getAppName().equalsIgnoreCase(ent.getAppName()) &&
                                        "APPROVED".equalsIgnoreCase(a.getApprovalStatus()) &&
                                        !isHigherPermission(ent.getPermissionLevel(), a.getGrantedPermission()));

                        if (!hasApprovedExemption) {
                            RiskLevel level = ent.getPermissionLevel() == PermissionLevel.ADMIN ? RiskLevel.HIGH : RiskLevel.MEDIUM;
                            boolean gate = level == RiskLevel.HIGH || confidenceScore < 0.75;

                            ReconciliationIssue issue = new ReconciliationIssue(
                                    username,
                                    emp.getFullName(),
                                    IssueType.EXCESSIVE_ACCESS_MOVER,
                                    level,
                                    ent.getAppName(),
                                    confidenceScore,
                                    String.format("Role mover (Previous: %s -> Current: %s) retaining excessive permission '%s' on %s.",
                                            emp.getPreviousRole(), emp.getCurrentRole(), ent.getPermissionLevel(), ent.getAppName()),
                                    String.format("PreviousRole=%s, CurrentRole=%s, EntitlementPermission=%s, PolicyAllowed=%s",
                                            emp.getPreviousRole(), emp.getCurrentRole(), ent.getPermissionLevel(), allowed != null ? allowed : "NONE"),
                                    60,
                                    "PROTOTYPE",
                                    gate
                            );
                            detectedIssues.add(saveAndGate(issue, emp, "DOWNGRADE_EXCESSIVE_PRIVILEGES", allowed != null ? allowed : PermissionLevel.NONE));
                        }
                    }
                }
            }

            // 3. Check Unapproved Privileged Access
            if (emp.getStatus() == EmploymentStatus.ACTIVE) {
                for (Entitlement ent : entitlements) {
                    if (ent.getPermissionLevel() == PermissionLevel.ADMIN || ent.getPermissionLevel() == PermissionLevel.WRITE) {
                        boolean hasApproval = approvals.stream().anyMatch(a ->
                                a.getAppName().equalsIgnoreCase(ent.getAppName()) &&
                                        "APPROVED".equalsIgnoreCase(a.getApprovalStatus()));

                        PermissionLevel expected = policyMap.get(ent.getAppName());
                        if (!hasApproval && (expected == null || isHigherPermission(ent.getPermissionLevel(), expected))) {
                            RiskLevel level = ent.getPermissionLevel() == PermissionLevel.ADMIN ? RiskLevel.HIGH : RiskLevel.MEDIUM;
                            boolean gate = level == RiskLevel.HIGH || confidenceScore < 0.75;

                            ReconciliationIssue issue = new ReconciliationIssue(
                                    username,
                                    emp.getFullName(),
                                    IssueType.UNAPPROVED_PRIVILEGED_ACCESS,
                                    level,
                                    ent.getAppName(),
                                    confidenceScore,
                                    String.format("User holding unapproved %s access on %s without documented approval record.",
                                            ent.getPermissionLevel(), ent.getAppName()),
                                    String.format("EntitlementPermission=%s, AppName=%s, DocumentedApprovalCount=0",
                                            ent.getPermissionLevel(), ent.getAppName()),
                                    120,
                                    "PROTOTYPE",
                                    gate
                            );
                            detectedIssues.add(saveAndGate(issue, emp, "REVOKE_UNAPPROVED_ACCESS", PermissionLevel.NONE));
                        }
                    }
                }
            }
        }

        auditService.logEvent("RECONCILIATION_RUN", "SYSTEM", "ENGINE",
                String.format("Executed prototype access reconciliation for %d users. Identified %d access issues with data confidence %.2f.",
                        employees.size(), detectedIssues.size(), confidenceScore));

        return detectedIssues;
    }

    private boolean isHigherPermission(PermissionLevel current, PermissionLevel policy) {
        if (policy == null || policy == PermissionLevel.NONE) return true;
        if (current == PermissionLevel.ADMIN && policy != PermissionLevel.ADMIN) return true;
        if (current == PermissionLevel.WRITE && policy == PermissionLevel.READ) return true;
        return false;
    }

    private ReconciliationIssue saveAndGate(ReconciliationIssue issue, Employee employee, String actionType, PermissionLevel targetLevel) {
        ReconciliationIssue saved = issueRepository.save(issue);

        RemediationStatus actionStatus = saved.getSafetyGateTriggered() ? RemediationStatus.PENDING_APPROVAL : RemediationStatus.APPROVED;

        RemediationAction action = new RemediationAction(
                saved.getId(),
                employee.getUsername(),
                employee.getFullName(),
                actionType,
                saved.getAppName(),
                actionStatus,
                PermissionLevel.ADMIN, // Default previous level
                targetLevel,
                saved.getDescription(),
                "RECON_ENGINE"
        );
        actionRepository.save(action);

        if (saved.getSafetyGateTriggered()) {
            auditService.logEvent("SAFETY_GATE_TRIGGERED", "RECON_ENGINE", employee.getUsername(),
                    String.format("Safety gate enforced for %s (%s on %s). Risk: %s, Confidence: %.2f. Action routed to approval queue.",
                            employee.getFullName(), saved.getIssueType(), saved.getAppName(), saved.getRiskLevel(), saved.getDataConfidenceScore()));
        }

        return saved;
    }

    public double calculateDataConfidence() {
        List<DataSourceHealth> feeds = healthRepository.findAll();
        if (feeds.isEmpty()) return 1.0;

        int stale = 0, delayed = 0, unavailable = 0;
        for (DataSourceHealth feed : feeds) {
            if (feed.getStatus() == HealthState.STALE) stale++;
            else if (feed.getStatus() == HealthState.DELAYED) delayed++;
            else if (feed.getStatus() == HealthState.UNAVAILABLE) unavailable++;
        }

        double confidence = 1.0 - (stale * 0.15) - (delayed * 0.25) - (unavailable * 0.40);
        return Math.max(0.0, Math.min(1.0, confidence));
    }
}
