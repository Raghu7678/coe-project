package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.ReconciliationIssue;
import com.jml.reconciliation.model.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReconciliationIssueRepository extends JpaRepository<ReconciliationIssue, Long> {
    List<ReconciliationIssue> findByDetectionEngine(String detectionEngine);
    List<ReconciliationIssue> findByDetectionEngineAndRiskLevel(String detectionEngine, RiskLevel riskLevel);
    void deleteByDetectionEngine(String detectionEngine);
}
