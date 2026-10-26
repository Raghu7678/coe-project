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

/**
 * REST Controller for Qualification Remediation, Instant Rollback & Evidence Reporting.
 * 
 * Manages the full remediation lifecycle:
 * 1. Execution: Applying approved permission revocations or downgrades in target application stores.
 * 2. Instant Rollback: Restoring previous permissions without data loss when false alarms occur.
 * 3. Stakeholder Validation: Digitally signing off and validating the removal evidence trail.
 * 4. Compliance Export: Generating formal CSV and printable HTML/PDF removal evidence reports.
 */
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

    /**
     * Lists all remediation actions and their current lifecycle statuses.
     * 
     * @return List of all RemediationAction records
     */
    @GetMapping
    public List<RemediationAction> getAllActions() {
        return remediationService.getAllActions();
    }

    /**
     * Executes an approved remediation action, updating entitlements in the database
     * and recording an audit event.
     * 
     * @param id primary key of the RemediationAction
     * @return updated RemediationAction with EXECUTED status
     */
    @PostMapping("/{id}/execute")
    public RemediationAction executeAction(@PathVariable Long id) {
        return remediationService.executeAction(id);
    }

    /**
     * Performs an instant rollback on an executed remediation action, restoring the user's
     * previous entitlement permission level while appending a rollback record to the audit trail.
     * 
     * @param id primary key of the executed RemediationAction
     * @param request optional rollback metadata (actor, reason)
     * @return updated RemediationAction with ROLLED_BACK status
     */
    @PostMapping("/{id}/rollback")
    public RemediationAction rollbackAction(@PathVariable Long id, @RequestBody(required = false) RollbackRequest request) {
        return remediationService.rollbackAction(id, request);
    }

    /**
     * Attaches a digital stakeholder verification signoff to an executed remediation action.
     * 
     * @param id primary key of the executed RemediationAction
     * @param request optional validator name and verification notes
     * @return updated RemediationAction with stakeholderValidated=true
     */
    @PostMapping("/{id}/validate")
    public RemediationAction validateAction(@PathVariable Long id, @RequestBody(required = false) StakeholderValidationRequest request) {
        String validator = (request != null && request.getValidatedBy() != null) ? request.getValidatedBy() : "STAKEHOLDER_AUDITOR";
        String notes = (request != null && request.getValidationNotes() != null) ? request.getValidationNotes() : "Evidence trail verified against access logs.";
        return remediationService.validateAction(id, validator, notes);
    }

    /**
     * Exports the removal evidence trail as an RFC 4180 CSV file for external audit teams.
     * 
     * @return downloadable CSV attachment
     */
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

    /**
     * Generates a printable removal evidence audit report in HTML format
     * formatted for instant PDF export / print via browser dialog.
     * 
     * @return downloadable/viewable HTML document
     */
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
