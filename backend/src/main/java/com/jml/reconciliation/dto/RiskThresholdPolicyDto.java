package com.jml.reconciliation.dto;

/**
 * Data Transfer Object representing the system-wide Risk Threshold Policy
 * and Multi-Source Risk Weightings.
 * 
 * Configures:
 * - Safety thresholds (EMR: Experience Modification Rate, TRIR: Total Recordable Incident Rate)
 * - Insurance coverage floor ($ Millions)
 * - Financial credit score floor
 * - Relative weighting formulas across the 4 primary feeds (Safety, Financial, Insurance, Sanctions)
 * - Confidence threshold for automated safety gate enforcement (default: 0.75)
 */
public class RiskThresholdPolicyDto {

    private Long id;
    private String policyName;
    private Double maxAllowedEmr;
    private Double maxAllowedTrir;
    private Double minInsuranceLimitMillion;
    private Integer minCreditScore;
    private Double safetyWeight;
    private Double financialWeight;
    private Double insuranceWeight;
    private Double sanctionWeight;
    private Double confidenceSafetyGateThreshold;

    public RiskThresholdPolicyDto() {
        // Sensible industry defaults
        this.id = 1L;
        this.policyName = "Enterprise Default Pre-Qualification & Access Policy";
        this.maxAllowedEmr = 1.25;
        this.maxAllowedTrir = 3.0;
        this.minInsuranceLimitMillion = 2.0;
        this.minCreditScore = 60;
        this.safetyWeight = 0.35;
        this.financialWeight = 0.25;
        this.insuranceWeight = 0.20;
        this.sanctionWeight = 0.20;
        this.confidenceSafetyGateThreshold = 0.75;
    }

    public RiskThresholdPolicyDto(Long id, String policyName, Double maxAllowedEmr, Double maxAllowedTrir,
                                 Double minInsuranceLimitMillion, Integer minCreditScore, Double safetyWeight,
                                 Double financialWeight, Double insuranceWeight, Double sanctionWeight,
                                 Double confidenceSafetyGateThreshold) {
        this.id = id;
        this.policyName = policyName;
        this.maxAllowedEmr = maxAllowedEmr;
        this.maxAllowedTrir = maxAllowedTrir;
        this.minInsuranceLimitMillion = minInsuranceLimitMillion;
        this.minCreditScore = minCreditScore;
        this.safetyWeight = safetyWeight;
        this.financialWeight = financialWeight;
        this.insuranceWeight = insuranceWeight;
        this.sanctionWeight = sanctionWeight;
        this.confidenceSafetyGateThreshold = confidenceSafetyGateThreshold;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public Double getMaxAllowedEmr() { return maxAllowedEmr; }
    public void setMaxAllowedEmr(Double maxAllowedEmr) { this.maxAllowedEmr = maxAllowedEmr; }

    public Double getMaxAllowedTrir() { return maxAllowedTrir; }
    public void setMaxAllowedTrir(Double maxAllowedTrir) { this.maxAllowedTrir = maxAllowedTrir; }

    public Double getMinInsuranceLimitMillion() { return minInsuranceLimitMillion; }
    public void setMinInsuranceLimitMillion(Double minInsuranceLimitMillion) { this.minInsuranceLimitMillion = minInsuranceLimitMillion; }

    public Integer getMinCreditScore() { return minCreditScore; }
    public void setMinCreditScore(Integer minCreditScore) { this.minCreditScore = minCreditScore; }

    public Double getSafetyWeight() { return safetyWeight; }
    public void setSafetyWeight(Double safetyWeight) { this.safetyWeight = safetyWeight; }

    public Double getFinancialWeight() { return financialWeight; }
    public void setFinancialWeight(Double financialWeight) { this.financialWeight = financialWeight; }

    public Double getInsuranceWeight() { return insuranceWeight; }
    public void setInsuranceWeight(Double insuranceWeight) { this.insuranceWeight = insuranceWeight; }

    public Double getSanctionWeight() { return sanctionWeight; }
    public void setSanctionWeight(Double sanctionWeight) { this.sanctionWeight = sanctionWeight; }

    public Double getConfidenceSafetyGateThreshold() { return confidenceSafetyGateThreshold; }
    public void setConfidenceSafetyGateThreshold(Double confidenceSafetyGateThreshold) { this.confidenceSafetyGateThreshold = confidenceSafetyGateThreshold; }
}
