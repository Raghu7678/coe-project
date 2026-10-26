package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.ApprovalDecisionRequest;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.service.ApprovalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Accountable Approvals & Dual-Control Governance.
 * 
 * Enforces dual-control multi-person integrity on remediation actions
 * triggered by high-risk access anomalies or degraded feed confidence scores.
 * 
 * Core Policy:
 * - Prevents self-approval: The reviewer approving an action MUST be distinct from the initiator.
 * - Records immutable approval audit records for compliance and forensic accountability.
 */
@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(originPatterns = "*")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * Retrieves all qualification / remediation actions currently held in the safety gate queue.
     * 
     * @return List of pending RemediationAction items awaiting risk officer decision
     */
    @GetMapping("/pending")
    public List<RemediationAction> getPendingApprovals() {
        return approvalService.getPendingApprovals();
    }

    /**
     * Submits an approval or rejection decision for a queued remediation action.
     * 
     * Enforces dual-control:
     * - Throws {@link IllegalArgumentException} if reviewerId matches action.initiatedBy.
     * - Transitions action status to APPROVED or REJECTED.
     * - Dispatches audit events into the immutable log.
     * 
     * @param id primary key of the pending RemediationAction
     * @param request decision details containing reviewerId, decision ('APPROVED'|'REJECTED'), and comments
     * @return updated RemediationAction entity
     */
    @PostMapping("/{id}/decision")
    public RemediationAction processDecision(@PathVariable Long id, @RequestBody ApprovalDecisionRequest request) {
        return approvalService.processDecision(id, request);
    }
}
