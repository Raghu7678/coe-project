package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.RolePolicy;
import com.jml.reconciliation.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public List<RolePolicy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/role/{roleName}")
    public List<RolePolicy> getPoliciesByRole(@PathVariable String roleName) {
        return policyService.getPoliciesForRole(roleName);
    }

    @PostMapping
    public ResponseEntity<RolePolicy> savePolicy(@Valid @RequestBody RolePolicy policy) {
        return ResponseEntity.ok(policyService.savePolicy(policy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
