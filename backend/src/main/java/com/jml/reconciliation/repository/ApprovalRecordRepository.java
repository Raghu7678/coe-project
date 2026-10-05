package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {
    List<ApprovalRecord> findByUsername(String username);
    List<ApprovalRecord> findByUsernameAndAppName(String username, String appName);
}
