package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.RolePolicy;
import com.jml.reconciliation.repository.RolePolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {

    private final RolePolicyRepository rolePolicyRepository;

    public PolicyService(RolePolicyRepository rolePolicyRepository) {
        this.rolePolicyRepository = rolePolicyRepository;
    }

    public List<RolePolicy> getAllPolicies() {
        return rolePolicyRepository.findAll();
    }

    public List<RolePolicy> getPoliciesByRole(String roleName) {
        return rolePolicyRepository.findByRoleName(roleName);
    }
}
