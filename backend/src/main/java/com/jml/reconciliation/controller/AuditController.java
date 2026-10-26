package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.service.AuditService;
import com.jml.reconciliation.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST Controller for the Immutable Audit Trail & Compliance Export.
 * 
 * Provides an append-only historical record of all significant security actions:
 * - Engine evaluation executions
 * - Safety gate trigger events
 * - Dual-control approval/rejection decisions
 * - Remediation state modifications and instant rollbacks
 * - Stakeholder signoff validations
 */
@RestController
@RequestMapping("/api/audit")
@CrossOrigin(originPatterns = "*")
public class AuditController {

    private final AuditService auditService;
    private final ExportService exportService;

    public AuditController(AuditService auditService, ExportService exportService) {
        this.auditService = auditService;
        this.exportService = exportService;
    }

    /**
     * Retrieves all immutable audit events in chronological order.
     * 
     * @return List of AuditEvent records
     */
    @GetMapping
    public List<AuditEvent> getAuditLogs() {
        return auditService.getAllAuditLogs();
    }

    /**
     * Exports the immutable audit log as an RFC 4180 compliant downloadable CSV file.
     * Useful for SOC 2 Type II, ISO 27001, and SOX internal audit reviews.
     * 
     * @return HTTP 200 ResponseEntity containing binary CSV stream with attachment headers
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportAuditCsv() {
        List<AuditEvent> events = auditService.getAllAuditLogs();
        String csvContent = exportService.generateAuditCsv(events);
        byte[] bytes = csvContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_trail_log.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
