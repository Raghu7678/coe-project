package com.jml.reconciliation.service;

import com.jml.reconciliation.dto.ApprovalDecisionRequest;
import com.jml.reconciliation.entity.ApprovalRecord;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.model.enums.RemediationStatus;
import com.jml.reconciliation.repository.ApprovalRecordRepository;
import com.jml.reconciliation.repository.RemediationActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service orchestrating dual-control accountable approval workflows.
 * 
 * Enforces dual-control separation of duty:
 * - When an access anomaly triggers a safety gate (due to CRITICAL risk or low feed confidence),
 *   the resulting remediation action is held in PENDING_APPROVAL.
 * - Any approval attempt by the initiating entity (e.g. initiator == reviewer) is rejected.
 * - Approved decisions transition the action to APPROVED, enabling execution.
 * - All decisions create immutable {@link ApprovalRecord} entries and append to the audit trail.
 */
@Service
public class ApprovalService {

    private final RemediationActionRepository actionRepository;
    private final ApprovalRecordRepository approvalRepository;
    private final AuditService auditService;

    public ApprovalService(
            RemediationActionRepository actionRepository,
            ApprovalRecordRepository approvalRepository,
            AuditService auditService) {
        this.actionRepository = actionRepository;
        this.approvalRepository = approvalRepository;
        this.auditService = auditService;
    }

    /**
     * Retrieves all remediation actions awaiting dual-control approval sign-off.
     * 
     * @return list of pending RemediationAction items
     */
    public List<RemediationAction> getPendingApprovals() {
        return actionRepository.findByStatus(RemediationStatus.PENDING_APPROVAL);
    }

    /**
     * Processes an approval decision submitted by a designated security or risk officer.
     * 
     * Validations:
     * 1. Action exists and is in PENDING_APPROVAL state.
     * 2. Reviewer ID is not identical to the action initiator (Dual-control enforcement).
     * 
     * @param actionId database ID of the remediation action
     * @param request decision payload containing reviewerId, decision ('APPROVED'|'REJECTED'), comments
     * @return updated RemediationAction entity
     */
    @Transactional
    public RemediationAction processDecision(Long actionId, ApprovalDecisionRequest request) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Remediation action not found with ID: " + actionId));

        if (action.getStatus() != RemediationStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Action is not in PENDING_APPROVAL status. Current status: " + action.getStatus());
        }

        // Enforce Accountable Approvals: Strict Self-Approval Prevention
        if (request.getReviewerId() != null && request.getReviewerId().equalsIgnoreCase(action.getInitiatedBy())) {
            throw new IllegalArgumentException("Self-approval is strictly forbidden under dual-control accountable approval policy.");
        }

        boolean isApproved = "APPROVED".equalsIgnoreCase(request.getDecision());

        // Create immutable approval record for forensics
        ApprovalRecord record = new ApprovalRecord(
                "APP-" + System.currentTimeMillis(),
                action.getUsername(),
                action.getAppName(),
                action.getTargetPermissionLevel(),
                action.getInitiatedBy(),
                request.getReviewerId(),
                LocalDateTime.now(),
                isApproved ? "APPROVED" : "REJECTED",
                request.getComments()
        );
        approvalRepository.save(record);

        // Update action status
        action.setApprovedBy(request.getReviewerId());
        action.setStatus(isApproved ? RemediationStatus.APPROVED : RemediationStatus.REJECTED);
        RemediationAction updated = actionRepository.save(action);

        // Append to immutable audit log
        auditService.logEvent(
                isApproved ? "APPROVAL_GRANTED" : "APPROVAL_REJECTED",
                request.getReviewerId(),
                action.getUsername(),
                String.format("Action #%d (%s on %s) for %s decision: %s. Rationale: %s",
                        actionId, action.getActionType(), action.getAppName(), action.getFullName(), isApproved ? "APPROVED" : "REJECTED", request.getComments())
        );

        return updated;
    }
}
