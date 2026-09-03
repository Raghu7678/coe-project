package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.model.enums.RemediationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemediationActionRepository extends JpaRepository<RemediationAction, String> {
    List<RemediationAction> findByStatus(RemediationStatus status);
    Optional<RemediationAction> findByIssueId(String issueId);
    List<RemediationAction> findByUserId(String userId);
}
