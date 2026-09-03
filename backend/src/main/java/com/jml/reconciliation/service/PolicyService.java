package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.RolePolicy;
import com.jml.reconciliation.model.enums.PermissionLevel;
import com.jml.reconciliation.repository.RolePolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    private final RolePolicyRepository rolePolicyRepository;

    public PolicyService(RolePolicyRepository rolePolicyRepository) {
        this.rolePolicyRepository = rolePolicyRepository;
    }

    public List<RolePolicy> getPoliciesForRole(String roleName) {
        return rolePolicyRepository.findByRoleName(roleName);
    }

    public PermissionLevel getExpectedPermission(String roleName, String applicationName) {
        Optional<RolePolicy> policyOpt = rolePolicyRepository.findByRoleNameAndApplicationName(roleName, applicationName);
        return policyOpt.map(RolePolicy::getExpectedPermission).orElse(PermissionLevel.NONE);
    }

    public List<RolePolicy> getAllPolicies() {
        return rolePolicyRepository.findAll();
    }

    public RolePolicy savePolicy(RolePolicy policy) {
        return rolePolicyRepository.save(policy);
    }

    public void deletePolicy(Long id) {
        rolePolicyRepository.deleteById(id);
    }
}
