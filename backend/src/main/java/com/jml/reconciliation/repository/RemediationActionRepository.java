package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.model.enums.RemediationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemediationActionRepository extends JpaRepository<RemediationAction, Long> {
    List<RemediationAction> findByStatus(RemediationStatus status);
}
