package com.jml.reconciliation.service;

import com.jml.reconciliation.dto.RiskThresholdPolicyDto;
import com.jml.reconciliation.entity.RolePolicy;
import com.jml.reconciliation.repository.RolePolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service managing access role policies and dynamic risk threshold policies.
 * 
 * Provides:
 * 1. Role-based entitlement mappings (RolePolicy) determining required permissions per application.
 * 2. Enterprise risk threshold limits (RiskThresholdPolicyDto) including EMR, TRIR, insurance minimums,
 *    credit ratings, and safety gate confidence thresholds.
 */
@Service
public class PolicyService {

    private final RolePolicyRepository rolePolicyRepository;
    private final AtomicReference<RiskThresholdPolicyDto> activeThresholdPolicy;

    public PolicyService(RolePolicyRepository rolePolicyRepository) {
        this.rolePolicyRepository = rolePolicyRepository;
        this.activeThresholdPolicy = new AtomicReference<>(new RiskThresholdPolicyDto());
    }

    /**
     * Retrieves all role policy entitlement rules.
     * @return list of configured RolePolicy entities
     */
    public List<RolePolicy> getAllPolicies() {
        return rolePolicyRepository.findAll();
    }

    /**
     * Retrieves role policy entitlement rules for a specific job title.
     * @param roleName employee role / title
     * @return list of matching RolePolicy entities
     */
    public List<RolePolicy> getPoliciesByRole(String roleName) {
        return rolePolicyRepository.findByRoleName(roleName);
    }

    /**
     * Retrieves the current active system-wide Risk Threshold Policy.
     * @return RiskThresholdPolicyDto containing active weights and safety thresholds
     */
    public RiskThresholdPolicyDto getRiskThresholdPolicy() {
        return activeThresholdPolicy.get();
    }

    /**
     * Updates the active system-wide Risk Threshold Policy.
     * @param updatedPolicy incoming policy settings
     * @return updated RiskThresholdPolicyDto
     */
    public RiskThresholdPolicyDto updateRiskThresholdPolicy(RiskThresholdPolicyDto updatedPolicy) {
        if (updatedPolicy == null) {
            throw new IllegalArgumentException("Policy configuration payload cannot be null.");
        }
        if (updatedPolicy.getConfidenceSafetyGateThreshold() != null &&
                (updatedPolicy.getConfidenceSafetyGateThreshold() < 0.0 || updatedPolicy.getConfidenceSafetyGateThreshold() > 1.0)) {
            throw new IllegalArgumentException("Confidence safety gate threshold must be bounded between 0.0 and 1.0.");
        }
        activeThresholdPolicy.set(updatedPolicy);
        return updatedPolicy;
    }
}
