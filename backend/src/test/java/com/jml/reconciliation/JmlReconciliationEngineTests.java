package com.jml.reconciliation;

import com.jml.reconciliation.dto.*;
import com.jml.reconciliation.entity.*;

import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import com.jml.reconciliation.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("default")
@Transactional
public class JmlReconciliationEngineTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private DirectoryGroupRepository directoryGroupRepository;

    @Autowired
    private ApprovalRecordRepository approvalRepository;

    @Autowired
    private ReconciliationIssueRepository issueRepository;

    @Autowired
    private RemediationActionRepository actionRepository;

    @Autowired
    private DataSourceHealthRepository healthRepository;

    @Autowired
    private ReconciliationEngineService reconciliationEngineService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private RemediationService remediationService;

    @BeforeEach
    public void setup() {
        // Data is pre-seeded by DataInitializer
    }

    @Test
    @DisplayName("Should detect orphaned access for leavers retaining active permissions")
    public void testOrphanedAccessLeaverDetection() {
        List<ReconciliationIssue> issues = issueRepository.findByDetectionEngine("PROTOTYPE");
        boolean foundLeaver = issues.stream()
                .anyMatch(i -> i.getIssueType() == IssueType.ORPHANED_ACCESS_LEAVER && "usr_sarah".equals(i.getUsername()));

        assertTrue(foundLeaver, "Orphaned access for leaver Sarah Jenkins should be detected.");
    }

    @Test
    @DisplayName("Should detect excessive privileges for role mover retaining leftover admin access")
    public void testExcessiveAccessMoverDetection() {
        List<ReconciliationIssue> issues = issueRepository.findByDetectionEngine("PROTOTYPE");
        boolean foundMover = issues.stream()
                .anyMatch(i -> i.getIssueType() == IssueType.EXCESSIVE_ACCESS_MOVER && "usr_alex".equals(i.getUsername()));

        assertTrue(foundMover, "Excessive access for role mover Alex Mercer should be detected.");
    }

    @Test
    @DisplayName("Should detect unapproved privileged access for active users without approval record")
    public void testUnapprovedPrivilegedAccessDetection() {
        List<ReconciliationIssue> issues = issueRepository.findByDetectionEngine("PROTOTYPE");
        boolean foundUnapproved = issues.stream()
                .anyMatch(i -> i.getIssueType() == IssueType.UNAPPROVED_PRIVILEGED_ACCESS && "usr_david".equals(i.getUsername()));

        assertTrue(foundUnapproved, "Unapproved AWS ADMIN access for David Vance should be detected.");
    }

    @Test
    @DisplayName("Should prevent self-approval under accountable approval dual-control policy")
    public void testSelfApprovalPrevention() {
        List<RemediationAction> pending = approvalService.getPendingApprovals();
        assertFalse(pending.isEmpty(), "Pending approvals queue should contain safety-gated actions.");

        RemediationAction action = pending.get(0);

        // Attempt self-approval (Reviewer ID = Initiator ID)
        ApprovalDecisionRequest selfRequest = new ApprovalDecisionRequest(action.getInitiatedBy(), "APPROVED", "Self approval test");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            approvalService.processDecision(action.getId(), selfRequest);
        });

        assertTrue(exception.getMessage().contains("Self-approval is strictly forbidden"), "System must block self-approval attempt.");
    }

    @Test
    @DisplayName("Should execute remediation action and successfully restore previous state upon rollback")
    public void testRemediationExecutionAndRollback() {
        List<RemediationAction> pending = approvalService.getPendingApprovals();
        RemediationAction action = pending.get(0);

        // 1. Process approval by distinct Security Officer
        RemediationAction approved = approvalService.processDecision(action.getId(), new ApprovalDecisionRequest("SEC_OFFICER_01", "APPROVED", "Approved for remediation"));
        assertEquals(RemediationStatus.APPROVED, approved.getStatus());

        // 2. Execute remediation action
        RemediationAction executed = remediationService.executeAction(approved.getId());
        assertEquals(RemediationStatus.EXECUTED, executed.getStatus());

        // 3. Trigger instant rollback
        RemediationAction rolledBack = remediationService.rollbackAction(executed.getId(), new RollbackRequest("AUDITOR_01", "False alarm override"));
        assertEquals(RemediationStatus.ROLLED_BACK, rolledBack.getStatus());

        Entitlement restored = entitlementRepository.findByUsernameAndAppName(executed.getUsername(), executed.getAppName()).orElse(null);
        assertNotNull(restored, "Entitlement must be restored upon rollback.");
    }

    @Test
    @DisplayName("Should degrade dynamic confidence score when data feeds become stale or unavailable")
    public void testDataFeedDegradationConfidenceScore() {
        double initialConfidence = reconciliationEngineService.calculateDataConfidence();
        assertEquals(1.0, initialConfidence, 0.01);

        // Degrade feed status
        DataSourceHealth hrFeed = healthRepository.findBySourceName("HR").orElseThrow();
        hrFeed.setStatus(HealthState.UNAVAILABLE);
        healthRepository.save(hrFeed);

        double degradedConfidence = reconciliationEngineService.calculateDataConfidence();
        assertEquals(0.60, degradedConfidence, 0.01, "Confidence should drop by 0.40 when HR feed becomes UNAVAILABLE.");
    }

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private NotificationService notificationService;

    @Test
    @DisplayName("Should execute defensible multi-trial experiment with SLA performance & error analysis")
    public void testMultiTrialExperimentEvaluation() {
        ExperimentResultDto experiment = evaluationService.runMultiTrialExperiment(10);
        assertNotNull(experiment);
        assertEquals(10, experiment.getTotalTrials());
        assertTrue(experiment.getMeanPrecision() > 95.0);
        assertTrue(experiment.getMeanRecall() > 90.0);
        assertFalse(experiment.getSlaPerformances().isEmpty());
        assertFalse(experiment.getErrorAnalyses().isEmpty());
    }

    @Test
    @DisplayName("Should stamp stakeholder evidence validation on remediation action")
    public void testStakeholderValidationSignOff() {
        List<RemediationAction> pending = approvalService.getPendingApprovals();
        RemediationAction action = pending.get(0);

        RemediationAction approved = approvalService.processDecision(action.getId(), new ApprovalDecisionRequest("SEC_OFFICER_02", "APPROVED", "Signoff"));
        RemediationAction executed = remediationService.executeAction(approved.getId());

        RemediationAction validated = remediationService.validateAction(executed.getId(), "AUDITOR_BOB", "Verified against IAM logs");
        assertTrue(validated.getStakeholderValidated());
        assertEquals("AUDITOR_BOB", validated.getValidatedBy());
        assertNotNull(validated.getValidatedAt());
    }

    @Test
    @DisplayName("Should generate CSV export for evidence trail")
    public void testEvidenceTrailCsvExport() {
        List<RemediationAction> actions = actionRepository.findAll();
        String csv = exportService.generateRemediationCsv(actions);
        assertNotNull(csv);
        assertTrue(csv.startsWith("Action ID,Issue ID,Username"));
    }

    @Autowired
    private PolicyService policyService;

    @Autowired
    private com.jml.reconciliation.config.GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("Should translate domain exceptions into standardized ApiErrorResponse via GlobalExceptionHandler")
    public void testGlobalExceptionHandlerApiErrorBoundary() {
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setRequestURI("/api/approvals/1/decision");

        // 1. Dual-Control self-approval violation (400 Bad Request)
        IllegalArgumentException selfApprovalEx = new IllegalArgumentException("Self-approval is strictly forbidden under dual-control accountable approval policy.");
        org.springframework.http.ResponseEntity<ApiErrorResponse> badRequestEntity = globalExceptionHandler.handleIllegalArgument(selfApprovalEx, request);
        assertEquals(400, badRequestEntity.getStatusCode().value());
        assertNotNull(badRequestEntity.getBody());
        assertEquals("DUAL_CONTROL_SELF_APPROVAL_VIOLATION", badRequestEntity.getBody().getErrorCode());
        assertTrue(badRequestEntity.getBody().getMessage().contains("Self-approval"));

        // 2. Illegal lifecycle state transition (409 Conflict)
        IllegalStateException stateEx = new IllegalStateException("Only APPROVED actions can be executed. Current status: PENDING_APPROVAL");
        org.springframework.http.ResponseEntity<ApiErrorResponse> conflictEntity = globalExceptionHandler.handleIllegalState(stateEx, request);
        assertEquals(409, conflictEntity.getStatusCode().value());
        assertNotNull(conflictEntity.getBody());
        assertEquals("INVALID_LIFECYCLE_STATE", conflictEntity.getBody().getErrorCode());

        // 3. Resource Not Found (404 Not Found)
        java.util.NoSuchElementException notFoundEx = new java.util.NoSuchElementException("Action not found with ID: 999");
        org.springframework.http.ResponseEntity<ApiErrorResponse> notFoundEntity = globalExceptionHandler.handleNotFound(notFoundEx, request);
        assertEquals(404, notFoundEntity.getStatusCode().value());
        assertNotNull(notFoundEntity.getBody());
        assertEquals("RESOURCE_NOT_FOUND", notFoundEntity.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should clamp confidence score to 0.0 floor when multiple feeds fail")
    public void testConfidenceScoreLowerBoundFloor() {
        List<DataSourceHealth> feeds = healthRepository.findAll();
        for (DataSourceHealth feed : feeds) {
            feed.setStatus(HealthState.UNAVAILABLE);
            healthRepository.save(feed);
        }

        double confidence = reconciliationEngineService.calculateDataConfidence();
        assertEquals(0.0, confidence, 0.001, "Confidence score must be clamped at 0.0 and never become negative.");
    }

    @Test
    @DisplayName("Should enforce 15-minute SLA target for critical leavers with ADMIN permissions")
    public void testCriticalLeaverSlaTargetMinutes() {
        List<ReconciliationIssue> issues = issueRepository.findByDetectionEngine("PROTOTYPE");
        ReconciliationIssue marcusIssue = issues.stream()
                .filter(i -> "usr_marcus".equals(i.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(marcusIssue, "Critical leaver Marcus Brody must have a detected issue.");
        assertEquals(RiskLevel.CRITICAL, marcusIssue.getRiskLevel(), "Marcus Brody holds AWS Console ADMIN and must be flagged CRITICAL.");
        assertEquals(15, marcusIssue.getSlaTargetMinutes(), "Critical risk leavers must be assigned 15-minute target SLA.");
        assertTrue(marcusIssue.getSafetyGateTriggered(), "Critical leaver must trigger mandatory safety gate.");
    }

    @Test
    @DisplayName("Should retrieve, validate, and update dynamic risk threshold policies")
    public void testRiskThresholdPolicyManagement() {
        RiskThresholdPolicyDto initial = policyService.getRiskThresholdPolicy();
        assertNotNull(initial);
        assertEquals(1.25, initial.getMaxAllowedEmr(), 0.01);

        RiskThresholdPolicyDto updated = new RiskThresholdPolicyDto(
                1L, "Custom Stricter Policy", 1.10, 2.5, 5.0, 75,
                0.40, 0.20, 0.20, 0.20, 0.85
        );
        policyService.updateRiskThresholdPolicy(updated);

        RiskThresholdPolicyDto active = policyService.getRiskThresholdPolicy();
        assertEquals("Custom Stricter Policy", active.getPolicyName());
        assertEquals(1.10, active.getMaxAllowedEmr(), 0.01);
        assertEquals(0.85, active.getConfidenceSafetyGateThreshold(), 0.01);

        // Validation test: confidence threshold out of bounds
        RiskThresholdPolicyDto invalid = new RiskThresholdPolicyDto();
        invalid.setConfidenceSafetyGateThreshold(1.50);
        assertThrows(IllegalArgumentException.class, () -> policyService.updateRiskThresholdPolicy(invalid));
    }
}
