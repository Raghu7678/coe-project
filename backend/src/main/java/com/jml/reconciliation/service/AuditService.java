package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.repository.AuditEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    private final AuditEventRepository auditRepository;

    public AuditService(AuditEventRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logEvent(String eventType, String actor, String targetEntity, String details) {
        AuditEvent event = new AuditEvent(eventType, actor, targetEntity, details);
        auditRepository.save(event);
    }

    public List<AuditEvent> getAllAuditLogs() {
        return auditRepository.findAllByOrderByTimestampDesc();
    }
}
