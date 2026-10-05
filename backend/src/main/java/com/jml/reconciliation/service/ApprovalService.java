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

    public List<RemediationAction> getPendingApprovals() {
        return actionRepository.findByStatus(RemediationStatus.PENDING_APPROVAL);
    }

    @Transactional
    public RemediationAction processDecision(Long actionId, ApprovalDecisionRequest request) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation action not found with ID: " + actionId));

        if (action.getStatus() != RemediationStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Action is not in PENDING_APPROVAL status. Current status: " + action.getStatus());
        }

        // Enforce Accountable Approvals: Self-Approval Prevention
        if (request.getReviewerId() != null && request.getReviewerId().equalsIgnoreCase(action.getInitiatedBy())) {
            throw new IllegalArgumentException("Self-approval is strictly forbidden under dual-control accountable approval policy.");
        }

        boolean isApproved = "APPROVED".equalsIgnoreCase(request.getDecision());

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

        action.setApprovedBy(request.getReviewerId());
        action.setStatus(isApproved ? RemediationStatus.APPROVED : RemediationStatus.REJECTED);
        RemediationAction updated = actionRepository.save(action);

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
