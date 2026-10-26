package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.RiskThresholdPolicyDto;
import com.jml.reconciliation.entity.RolePolicy;
import com.jml.reconciliation.service.PolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Policy & Threshold Management.
 * 
 * Exposes endpoints for:
 * 1. Managing dynamic safety gate thresholds, weights, and EMR/TRIR limits (/api/policy).
 * 2. Querying Role-Based Access Control (RBAC) role policy rules (/api/policy/roles).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /**
     * Retrieves the current system-wide risk threshold and weighting policy.
     * @return current RiskThresholdPolicyDto configuration
     */
    @GetMapping("/policy")
    public RiskThresholdPolicyDto getActiveThresholdPolicy() {
        return policyService.getRiskThresholdPolicy();
    }

    /**
     * Updates the system-wide risk threshold configuration.
     * @param request modified policy values
     * @return updated RiskThresholdPolicyDto
     */
    @PutMapping("/policy")
    public RiskThresholdPolicyDto updateThresholdPolicy(@RequestBody RiskThresholdPolicyDto request) {
        return policyService.updateRiskThresholdPolicy(request);
    }

    /**
     * Retrieves all Role-Based Access Control (RBAC) role policies.
     * @return list of RolePolicy entities
     */
    @GetMapping({"/policy/roles", "/policies"})
    public List<RolePolicy> getAllRolePolicies() {
        return policyService.getAllPolicies();
    }

    /**
     * Retrieves role policy specifications for a particular employee title.
     * @param roleName employee role
     * @return list of RolePolicy rules
     */
    @GetMapping("/policy/roles/{roleName}")
    public List<RolePolicy> getPoliciesByRole(@PathVariable String roleName) {
        return policyService.getPoliciesByRole(roleName);
    }
}
