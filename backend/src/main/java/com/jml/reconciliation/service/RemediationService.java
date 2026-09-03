package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.*;
import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RemediationService {

    private final RemediationActionRepository remediationRepository;
    private final ReconciliationIssueRepository issueRepository;
    private final EntitlementRepository entitlementRepository;
    private final DirectoryGroupRepository directoryGroupRepository;
    private final AuditService auditService;

    public RemediationService(RemediationActionRepository remediationRepository,
                              ReconciliationIssueRepository issueRepository,
                              EntitlementRepository entitlementRepository,
                              DirectoryGroupRepository directoryGroupRepository,
                              AuditService auditService) {
        this.remediationRepository = remediationRepository;
        this.issueRepository = issueRepository;
        this.entitlementRepository = entitlementRepository;
        this.directoryGroupRepository = directoryGroupRepository;
        this.auditService = auditService;
    }

    public List<RemediationAction> getAllRemediations() {
        return remediationRepository.findAll();
    }

    public Optional<RemediationAction> getRemediationById(String id) {
        return remediationRepository.findById(id);
    }

    @Transactional
    public RemediationAction executeRemediation(String remediationId, String actor) {
        RemediationAction remediation = remediationRepository.findById(remediationId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation not found: " + remediationId));

        if (remediation.getStatus() == RemediationStatus.EXECUTED) {
            throw new IllegalStateException("Remediation has already been executed");
        }

        ReconciliationIssue issue = issueRepository.findById(remediation.getIssueId())
                .orElseThrow(() -> new IllegalArgumentException("Associated issue not found: " + remediation.getIssueId()));

        String userId = remediation.getUserId();
        String app = remediation.getApplicationName();
        RemediationActionType actionType = remediation.getActionType();

        try {
            // Apply actual access modification in simulated application entitlement data store
            if (actionType == RemediationActionType.REMOVE_ACCESS) {
                Optional<Entitlement> entOpt = entitlementRepository.findByUserIdAndApplicationName(userId, app);
                entOpt.ifPresent(ent -> {
                    ent.setPermissionLevel(PermissionLevel.NONE);
                    ent.setStatus("REVOKED");
                    ent.setLastUpdated(LocalDateTime.now());
                    entitlementRepository.save(ent);
                });
            } else if (actionType == RemediationActionType.REDUCE_PERMISSION) {
                Optional<Entitlement> entOpt = entitlementRepository.findByUserIdAndApplicationName(userId, app);
                entOpt.ifPresent(ent -> {
                    ent.setPermissionLevel(issue.getExpectedAccess());
                    ent.setLastUpdated(LocalDateTime.now());
                    entitlementRepository.save(ent);
                });
            } else if (actionType == RemediationActionType.ADD_ACCESS) {
                Entitlement ent = entitlementRepository.findByUserIdAndApplicationName(userId, app)
                        .orElse(new Entitlement(userId, app, issue.getExpectedAccess(), LocalDateTime.now(), "ACTIVE", LocalDateTime.now()));
                ent.setPermissionLevel(issue.getExpectedAccess());
                ent.setStatus("ACTIVE");
                ent.setLastUpdated(LocalDateTime.now());
                entitlementRepository.save(ent);
            } else if (actionType == RemediationActionType.REMOVE_FROM_GROUP) {
                List<DirectoryGroup> groups = directoryGroupRepository.findByUserIdAndApplicationName(userId, app);
                directoryGroupRepository.deleteAll(groups);
            }

            LocalDateTime now = LocalDateTime.now();
            remediation.setStatus(RemediationStatus.EXECUTED);
            remediation.setExecutedAt(now);
            RemediationAction savedRemediation = remediationRepository.save(remediation);

            // Calculate remediation target time compliance
            int durationMinutes = (int) Duration.between(issue.getDetectedAt(), now).toMinutes();
            boolean metTarget = durationMinutes <= (issue.getTargetTimeMinutes() != null ? issue.getTargetTimeMinutes() : 1440);

            issue.setStatus("RESOLVED");
            issue.setRemediatedAt(now);
            issue.setRemediationTimeMinutes(Math.max(1, durationMinutes));
            issue.setMetTarget(metTarget);
            issueRepository.save(issue);

            EventType eventType = (actionType == RemediationActionType.REMOVE_ACCESS) ? EventType.ACCESS_REMOVED : EventType.ACCESS_REDUCED;
            auditService.logEvent(
                    eventType,
                    userId,
                    actor,
                    "EXECUTE_REMEDIATION",
                    remediation.getPreviousState(),
                    remediation.getProposedState(),
                    "Remediation executed successfully for issue " + issue.getIssueId(),
                    issue.getIssueId(),
                    remediation.getRemediationId(),
                    "APPLICATION_ENTITLEMENTS"
            );

            return savedRemediation;

        } catch (Exception e) {
            remediation.setStatus(RemediationStatus.FAILED);
            remediationRepository.save(remediation);
            
            auditService.logEvent(
                    EventType.ISSUE_DETECTED,
                    userId,
                    actor,
                    "REMEDIATION_FAILED",
                    remediation.getPreviousState(),
                    "FAILED",
                    "Remediation execution failed: " + e.getMessage(),
                    issue.getIssueId(),
                    remediation.getRemediationId(),
                    "APPLICATION_ENTITLEMENTS"
            );
            throw new RuntimeException("Remediation execution failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RemediationAction rollbackRemediation(String remediationId, String actor, String reason) {
        RemediationAction remediation = remediationRepository.findById(remediationId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation not found: " + remediationId));

        if (remediation.getStatus() != RemediationStatus.EXECUTED) {
            throw new IllegalStateException("Only EXECUTED remediations can be rolled back");
        }

        ReconciliationIssue issue = issueRepository.findById(remediation.getIssueId())
                .orElseThrow(() -> new IllegalArgumentException("Associated issue not found: " + remediation.getIssueId()));

        String userId = remediation.getUserId();
        String app = remediation.getApplicationName();

        // Parse previous state permission level (e.g. "Permission: ADMIN")
        PermissionLevel previousPermission = PermissionLevel.NONE;
        if (remediation.getPreviousState().contains("ADMIN")) previousPermission = PermissionLevel.ADMIN;
        else if (remediation.getPreviousState().contains("WRITE")) previousPermission = PermissionLevel.WRITE;
        else if (remediation.getPreviousState().contains("USER")) previousPermission = PermissionLevel.USER;
        else if (remediation.getPreviousState().contains("READ")) previousPermission = PermissionLevel.READ;

        // Restore entitlement state to previous permission level
        Optional<Entitlement> entOpt = entitlementRepository.findByUserIdAndApplicationName(userId, app);
        if (entOpt.isPresent()) {
            Entitlement ent = entOpt.get();
            ent.setPermissionLevel(previousPermission);
            ent.setStatus("ACTIVE");
            ent.setLastUpdated(LocalDateTime.now());
            entitlementRepository.save(ent);
        } else {
            Entitlement ent = new Entitlement(userId, app, previousPermission, LocalDateTime.now(), "ACTIVE", LocalDateTime.now());
            entitlementRepository.save(ent);
        }

        remediation.setStatus(RemediationStatus.ROLLED_BACK);
        remediation.setRolledBackAt(LocalDateTime.now());
        remediation.setRollbackReason(reason);
        RemediationAction savedRemediation = remediationRepository.save(remediation);

        issue.setStatus("ROLLED_BACK");
        issueRepository.save(issue);

        // Immutable Audit Trail - append new ROLLBACK_EXECUTED event
        auditService.logEvent(
                EventType.ROLLBACK_EXECUTED,
                userId,
                actor,
                "ROLLBACK_REMEDIATION",
                remediation.getProposedState(),
                remediation.getPreviousState(),
                "Rollback executed: " + reason,
                issue.getIssueId(),
                remediation.getRemediationId(),
                "APPLICATION_ENTITLEMENTS"
        );

        return savedRemediation;
    }
}
