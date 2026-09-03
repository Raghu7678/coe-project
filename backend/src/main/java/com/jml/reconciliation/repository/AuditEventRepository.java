package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.model.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {
    List<AuditEvent> findByUserIdOrderByTimestampDesc(String userId);
    List<AuditEvent> findByEventTypeOrderByTimestampDesc(EventType eventType);
    List<AuditEvent> findAllByOrderByTimestampDesc();
}
