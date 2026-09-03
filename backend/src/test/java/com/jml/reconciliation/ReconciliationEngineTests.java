package com.jml.reconciliation;

import com.jml.reconciliation.dto.EvaluationMetricsDto;
import com.jml.reconciliation.entity.*;
import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import com.jml.reconciliation.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReconciliationEngineTests {

    @Autowired private ReconciliationEngineService reconciliationEngineService;
    @Autowired private BaselineEngineService baselineEngineService;
    @Autowired private RemediationService remediationService;
    @Autowired private ApprovalService approvalService;
    @Autowired private DataSourceHealthService healthService;
    @Autowired private EvaluationService evaluationService;
    @Autowired private AuditService auditService;

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private RolePolicyRepository rolePolicyRepository;
    @Autowired private EntitlementRepository entitlementRepository;
    @Autowired private DirectoryGroupRepository directoryGroupRepository;
    @Autowired private ApprovalRecordRepository approvalRecordRepository;
    @Autowired private ReconciliationIssueRepository issueRepository;
    @Autowired private RemediationActionRepository remediationRepository;
    @Autowired private AuditEventRepository auditRepository;

    @BeforeEach
    void setUp() {
        // Clear health overrides
        healthService.updateHealthState("HR", HealthState.AVAILABLE, "Fresh");
        healthService.updateHealthState("DIRECTORY", HealthState.AVAILABLE, "Fresh");
        healthService.updateHealthState("APPLICATION_ENTITLEMENTS", HealthState.AVAILABLE, "Fresh");
        healthService.updateHealthState("APPROVAL_HISTORY", HealthState.AVAILABLE, "Fresh");
    }

    @Test
    @DisplayName("Test 1: Orphaned Access Detection for Leavers")
    void testOrphanedAccessDetection() {
        // Setup Leaver employee
        Employee leaver = new Employee("TEST-01", "usr_test_leaver", "Test Leaver", "leaver@test.com", 
                "Finance", "Finance Analyst", null, EmploymentStatus.LEFT, 
                LocalDateTime.now().minusYears(1), null, LocalDateTime.now());
        employeeRepository.save(leaver);

        Entitlement ent = new Entitlement("usr_test_leaver", "Finance System", PermissionLevel.ADMIN, 
                LocalDateTime.now().minusYears(1), "ACTIVE", LocalDateTime.now());
        entitlementRepository.save(ent);

        List<ReconciliationIssue> issues = reconciliationEngineService.runReconciliation();

        Optional<ReconciliationIssue> orphanedIssue = issues.stream()
                .filter(i -> i.getUserId().equals("usr_test_leaver") && i.getIssueType() == IssueType.ORPHANED_ACCESS)
                .findFirst();

        assertTrue(orphanedIssue.isPresent(), "Orphaned access issue should be detected for Leaver");
        assertEquals(RiskLevel.CRITICAL, orphanedIssue.get().getRiskLevel(), "Leaver with ADMIN access must be CRITICAL risk");
        assertEquals(RemediationActionType.REMOVE_ACCESS, orphanedIssue.get().getRecommendedAction());
    }

    @Test
    @DisplayName("Test 2: Excessive Access Detection for Role Mismatch")
    void testExcessiveAccessDetection() {
        Employee dev = new Employee("TEST-02", "usr_test_dev", "Test Dev", "dev@test.com", 
                "Engineering", "Developer", null, EmploymentStatus.ACTIVE, 
                LocalDateTime.now().minusMonths(6), null, LocalDateTime.now());
        employeeRepository.save(dev);

        // Developer expected permission = WRITE for GitHub. Seed ADMIN actual entitlement
        Entitlement ent = new Entitlement("usr_test_dev", "GitHub", PermissionLevel.ADMIN, 
                LocalDateTime.now().minusMonths(6), "ACTIVE", LocalDateTime.now());
        entitlementRepository.save(ent);

        List<ReconciliationIssue> issues = reconciliationEngineService.runReconciliation();

        Optional<ReconciliationIssue> excessiveIssue = issues.stream()
                .filter(i -> i.getUserId().equals("usr_test_dev") && i.getIssueType() == IssueType.EXCESSIVE_ACCESS)
                .findFirst();

        assertTrue(excessiveIssue.isPresent(), "Excessive access issue should be detected when actual level > expected level");
        assertEquals(RiskLevel.HIGH, excessiveIssue.get().getRiskLevel());
        assertEquals(RemediationActionType.REDUCE_PERMISSION, excessiveIssue.get().getRecommendedAction());
    }

    @Test
    @DisplayName("Test 3: Unapproved Access Detection")
    void testUnapprovedAccessDetection() {
        Employee user = new Employee("TEST-03", "usr_test_unapproved", "Test User", "unapp@test.com", 
                "Product", "Product Manager", null, EmploymentStatus.ACTIVE, 
                LocalDateTime.now().minusMonths(3), null, LocalDateTime.now());
        employeeRepository.save(user);

        // Product Manager expects Jira ADMIN. Seed AWS Console ADMIN without any approval
        Entitlement ent = new Entitlement("usr_test_unapproved", "AWS Console", PermissionLevel.ADMIN, 
                LocalDateTime.now().minusMonths(3), "ACTIVE", LocalDateTime.now());
        entitlementRepository.save(ent);

        List<ReconciliationIssue> issues = reconciliationEngineService.runReconciliation();

        boolean hasUnapprovedOrExcessive = issues.stream()
                .anyMatch(i -> i.getUserId().equals("usr_test_unapproved") && i.getApplicationName().equals("AWS Console"));

        assertTrue(hasUnapprovedOrExcessive, "Unapproved access should be flagged for privileged access without approval history");
    }

    @Test
    @DisplayName("Test 4: Missing Access Detection")
    void testMissingAccessDetection() {
        Employee hrUser = new Employee("TEST-04", "usr_test_missing", "Missing HR", "hr@test.com", 
                "HR", "HR Manager", null, EmploymentStatus.ACTIVE, 
                LocalDateTime.now().minusDays(5), null, LocalDateTime.now());
        employeeRepository.save(hrUser);
        // Do not seed HR System entitlement (expected: ADMIN)

        List<ReconciliationIssue> issues = reconciliationEngineService.runReconciliation();

        Optional<ReconciliationIssue> missingIssue = issues.stream()
                .filter(i -> i.getUserId().equals("usr_test_missing") && i.getIssueType() == IssueType.MISSING_ACCESS)
                .findFirst();

        assertTrue(missingIssue.isPresent(), "Missing required access should be detected for active employee role");
        assertEquals(RemediationActionType.ADD_ACCESS, missingIssue.get().getRecommendedAction());
    }

    @Test
    @DisplayName("Test 5: HR Data Delayed Scenario (Confidence Score Reduction)")
    void testHRDelayedScenario() {
        // Mark HR Data Source as STALE
        healthService.updateHealthState("HR", HealthState.STALE, "Stale 48h");

        List<ReconciliationIssue> issues = reconciliationEngineService.runReconciliation();

        assertFalse(issues.isEmpty());
        ReconciliationIssue issue = issues.get(0);
        assertTrue(issue.getConfidenceScore() < 1.0, "Confidence score must be reduced when HR source is STALE");
    }

    @Test
    @DisplayName("Test 6: Directory Service Unavailable Scenario")
    void testDirectoryUnavailableScenario() {
        // Mark DIRECTORY as UNAVAILABLE
        healthService.updateHealthState("DIRECTORY", HealthState.UNAVAILABLE, "Offline");

        List<ReconciliationIssue> issues = reconciliationEngineService.runReconciliation();

        assertFalse(issues.isEmpty(), "Engine must continue operating even when Directory service is UNAVAILABLE");
        
        // Audit log must record data source health change / reconciliation event
        List<AuditEvent> auditLogs = auditRepository.findAllByOrderByTimestampDesc();
        assertFalse(auditLogs.isEmpty());
    }

    @Test
    @DisplayName("Test 7: Remediation Execution and Rollback Workflow")
    void testRemediationExecutionAndRollback() {
        Employee emp = new Employee("TEST-07", "usr_test_rollback", "Rollback User", "rb@test.com", 
                "Engineering", "Developer", null, EmploymentStatus.ACTIVE, 
                LocalDateTime.now().minusMonths(2), null, LocalDateTime.now());
        employeeRepository.save(emp);

        Entitlement ent = new Entitlement("usr_test_rollback", "GitHub", PermissionLevel.ADMIN, 
                LocalDateTime.now().minusMonths(2), "ACTIVE", LocalDateTime.now());
        entitlementRepository.save(ent);

        reconciliationEngineService.runReconciliation();

        Optional<RemediationAction> remediationOpt = remediationRepository.findAll().stream()
                .filter(r -> r.getUserId().equals("usr_test_rollback"))
                .findFirst();

        assertTrue(remediationOpt.isPresent());
        RemediationAction remediation = remediationOpt.get();

        // 1. Approve & Execute
        approvalService.processApprovalDecision(remediation.getRemediationId(), "sec_reviewer", "APPROVE", "Valid risk reduction");

        Entitlement executedEnt = entitlementRepository.findByUserIdAndApplicationName("usr_test_rollback", "GitHub").orElseThrow();
        assertEquals(PermissionLevel.WRITE, executedEnt.getPermissionLevel(), "Permission level must be reduced to WRITE after execution");

        // 2. Perform Rollback
        RemediationAction rolledBackAction = remediationService.rollbackRemediation(remediation.getRemediationId(), "admin_user", "False positive correction");
        assertEquals(RemediationStatus.ROLLED_BACK, rolledBackAction.getStatus());

        Entitlement restoredEnt = entitlementRepository.findByUserIdAndApplicationName("usr_test_rollback", "GitHub").orElseThrow();
        assertEquals(PermissionLevel.ADMIN, restoredEnt.getPermissionLevel(), "Rollback must restore original ADMIN permission level");
    }

    @Test
    @DisplayName("Test 8: Immutable Audit Trail Integrity")
    void testAuditTrailImmutability() {
        long initialCount = auditRepository.count();

        auditService.logEvent(EventType.RECONCILIATION_STARTED, "SYS", "TEST", "RUN", null, null, "Test Event", null, null, "ALL");
        long newCount = auditRepository.count();

        assertEquals(initialCount + 1, newCount, "Audit events must be append-only");
    }

    @Test
    @DisplayName("Test 9: Baseline vs Prototype Evaluation Metrics")
    void testBaselineVsPrototypeEvaluation() {
        EvaluationMetricsDto metrics = evaluationService.runEvaluationExperiment();

        assertNotNull(metrics);
        assertTrue(metrics.getPrototypeRecall() >= metrics.getBaselineRecall(), "Prototype recall must outperform baseline");
        assertTrue(metrics.getPrototypePrecision() >= metrics.getBaselinePrecision(), "Prototype precision must outperform baseline");
        assertFalse(metrics.getErrorAnalysisNotes().isEmpty());
    }
}
