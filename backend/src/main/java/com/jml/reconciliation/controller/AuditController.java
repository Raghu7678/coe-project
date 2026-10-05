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

    @GetMapping
    public List<AuditEvent> getAuditLogs() {
        return auditService.getAllAuditLogs();
    }

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
