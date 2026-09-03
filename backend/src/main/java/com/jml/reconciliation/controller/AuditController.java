package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.model.enums.EventType;
import com.jml.reconciliation.service.AuditService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditEvent> getAuditTrail(@RequestParam(required = false) String userId,
                                          @RequestParam(required = false) String eventType) {
        if (userId != null && !userId.isBlank()) {
            return auditService.getAuditEventsByUserId(userId);
        }
        if (eventType != null && !eventType.isBlank()) {
            try {
                return auditService.getAuditEventsByType(EventType.valueOf(eventType.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        return auditService.getAllAuditEvents();
    }
}
