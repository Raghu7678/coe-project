package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.RolePolicy;
import com.jml.reconciliation.service.PolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policy")
@CrossOrigin(originPatterns = "*")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public List<RolePolicy> getAllPolicies() {
        return policyService.getAllPolicies();
    }
}
