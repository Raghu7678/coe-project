package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.entity.ReconciliationIssue;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.model.enums.EventType;
import com.jml.reconciliation.model.enums.RemediationStatus;
import com.jml.reconciliation.repository.ReconciliationIssueRepository;
import com.jml.reconciliation.repository.RemediationActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalService {

    private final RemediationActionRepository remediationRepository;
    private final ReconciliationIssueRepository issueRepository;
    private final RemediationService remediationService;
    private final AuditService auditService;

    public ApprovalService(RemediationActionRepository remediationRepository,
                           ReconciliationIssueRepository issueRepository,
                           RemediationService remediationService,
                           AuditService auditService) {
        this.remediationRepository = remediationRepository;
        this.issueRepository = issueRepository;
        this.remediationService = remediationService;
        this.auditService = auditService;
    }

    public List<RemediationAction> getPendingApprovals() {
        return remediationRepository.findByStatus(RemediationStatus.PENDING_REVIEW);
    }

    @Transactional
    public RemediationAction processApprovalDecision(String remediationId, String reviewerId, String decision, String comment) {
        RemediationAction remediation = remediationRepository.findById(remediationId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation action not found with ID: " + remediationId));

        if (remediation.getStatus() != RemediationStatus.PENDING_REVIEW && remediation.getStatus() != RemediationStatus.RECOMMENDED) {
            throw new IllegalStateException("Remediation is not in PENDING_REVIEW or RECOMMENDED status");
        }

        // Enforce Accountable Approval (Prevent requester approving own action if applicable)
        if (reviewerId != null && reviewerId.equalsIgnoreCase(remediation.getRequestedBy())) {
            throw new IllegalArgumentException("Accountable Approval Violation: Requester cannot approve their own remediation action");
        }

        remediation.setApprovedBy(reviewerId);
        remediation.setReviewerComment(comment);

        if ("APPROVE".equalsIgnoreCase(decision)) {
            remediation.setStatus(RemediationStatus.APPROVED);
            remediationRepository.save(remediation);

            auditService.logEvent(
                    EventType.APPROVAL_GRANTED,
                    remediation.getUserId(),
                    reviewerId,
                    "APPROVE_REMEDIATION",
                    "Status: PENDING_REVIEW",
                    "Status: APPROVED",
                    comment,
                    remediation.getIssueId(),
                    remediation.getRemediationId(),
                    "HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY"
            );

            // Execute the remediation immediately upon approval
            return remediationService.executeRemediation(remediation.getRemediationId(), reviewerId);

        } else if ("REJECT".equalsIgnoreCase(decision)) {
            remediation.setStatus(RemediationStatus.REJECTED);
            RemediationAction saved = remediationRepository.save(remediation);

            // Update associated issue status
            issueRepository.findById(remediation.getIssueId()).ifPresent(issue -> {
                issue.setStatus("REJECTED");
                issueRepository.save(issue);
            });

            auditService.logEvent(
                    EventType.APPROVAL_REJECTED,
                    remediation.getUserId(),
                    reviewerId,
                    "REJECT_REMEDIATION",
                    "Status: PENDING_REVIEW",
                    "Status: REJECTED",
                    comment,
                    remediation.getIssueId(),
                    remediation.getRemediationId(),
                    "HR, DIRECTORY, APPLICATION_ENTITLEMENTS, APPROVAL_HISTORY"
            );

            return saved;
        } else {
            throw new IllegalArgumentException("Invalid decision value. Expected 'APPROVE' or 'REJECT'");
        }
    }
}
