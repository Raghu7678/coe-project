package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.ApprovalRecord;
import com.jml.reconciliation.model.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, String> {
    List<ApprovalRecord> findByUserId(String userId);
    List<ApprovalRecord> findByUserIdAndApplicationName(String userId, String applicationName);
    List<ApprovalRecord> findByUserIdAndApplicationNameAndStatus(String userId, String applicationName, ApprovalStatus status);
}
