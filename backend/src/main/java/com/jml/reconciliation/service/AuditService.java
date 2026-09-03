package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.model.enums.EventType;
import com.jml.reconciliation.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public AuditEvent logEvent(EventType eventType, String userId, String actor, String action, 
                               String previousState, String newState, String reason, 
                               String relatedIssueId, String relatedApprovalId, String dataSourcesUsed) {
        AuditEvent event = new AuditEvent(
                "AUD-" + UUID.randomUUID().toString().substring(0, 8),
                LocalDateTime.now(),
                eventType,
                userId,
                actor,
                action,
                previousState,
                newState,
                reason,
                relatedIssueId,
                relatedApprovalId,
                dataSourcesUsed
        );
        return auditEventRepository.save(event);
    }

    public List<AuditEvent> getAllAuditEvents() {
        return auditEventRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditEvent> getAuditEventsByUserId(String userId) {
        return auditEventRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<AuditEvent> getAuditEventsByType(EventType eventType) {
        return auditEventRepository.findByEventTypeOrderByTimestampDesc(eventType);
    }
}
