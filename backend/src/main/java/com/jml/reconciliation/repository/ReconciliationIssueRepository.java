package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.ReconciliationIssue;
import com.jml.reconciliation.model.enums.EngineType;
import com.jml.reconciliation.model.enums.IssueType;
import com.jml.reconciliation.model.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReconciliationIssueRepository extends JpaRepository<ReconciliationIssue, String> {
    List<ReconciliationIssue> findByEngineType(EngineType engineType);
    List<ReconciliationIssue> findByEngineTypeAndRiskLevel(EngineType engineType, RiskLevel riskLevel);
    List<ReconciliationIssue> findByEngineTypeAndIssueType(EngineType engineType, IssueType issueType);
    List<ReconciliationIssue> findByEngineTypeAndStatus(EngineType engineType, String status);
    void deleteByEngineType(EngineType engineType);
}
