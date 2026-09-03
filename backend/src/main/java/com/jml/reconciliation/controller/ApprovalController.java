package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.ApprovalDecisionRequest;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.service.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public List<RemediationAction> getPendingApprovals() {
        return approvalService.getPendingApprovals();
    }

    @PostMapping("/{id}/decision")
    public ResponseEntity<RemediationAction> processDecision(@PathVariable String id, 
                                                             @Valid @RequestBody ApprovalDecisionRequest request) {
        RemediationAction result = approvalService.processApprovalDecision(
                id, request.getReviewerId(), request.getDecision(), request.getComment()
        );
        return ResponseEntity.ok(result);
    }
}
