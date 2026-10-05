package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.RollbackRequest;
import com.jml.reconciliation.dto.StakeholderValidationRequest;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.service.ExportService;
import com.jml.reconciliation.service.RemediationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/remediations")
@CrossOrigin(originPatterns = "*")
public class RemediationController {

    private final RemediationService remediationService;
    private final ExportService exportService;

    public RemediationController(RemediationService remediationService, ExportService exportService) {
        this.remediationService = remediationService;
        this.exportService = exportService;
    }

    @GetMapping
    public List<RemediationAction> getAllActions() {
        return remediationService.getAllActions();
    }

    @PostMapping("/{id}/execute")
    public RemediationAction executeAction(@PathVariable Long id) {
        return remediationService.executeAction(id);
    }

    @PostMapping("/{id}/rollback")
    public RemediationAction rollbackAction(@PathVariable Long id, @RequestBody(required = false) RollbackRequest request) {
        return remediationService.rollbackAction(id, request);
    }

    @PostMapping("/{id}/validate")
    public RemediationAction validateAction(@PathVariable Long id, @RequestBody(required = false) StakeholderValidationRequest request) {
        String validator = (request != null) ? request.getValidatedBy() : "STAKEHOLDER_AUDITOR";
        String notes = (request != null) ? request.getValidationNotes() : "Evidence trail verified.";
        return remediationService.validateAction(id, validator, notes);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportRemediationsCsv() {
        List<RemediationAction> actions = remediationService.getAllActions();
        String csvContent = exportService.generateRemediationCsv(actions);
        byte[] bytes = csvContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"removal_evidence_trail.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportRemediationsPdf() {
        List<RemediationAction> actions = remediationService.getAllActions();
        String htmlContent = exportService.generateRemediationPdfHtml(actions);
        byte[] bytes = htmlContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"removal_evidence_report.html\"")
                .contentType(MediaType.TEXT_HTML)
                .body(bytes);
    }
}
