package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.ApprovalDecisionRequest;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.service.ApprovalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(originPatterns = "*")
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
    public RemediationAction processDecision(@PathVariable Long id, @RequestBody ApprovalDecisionRequest request) {
        return approvalService.processDecision(id, request);
    }
}
