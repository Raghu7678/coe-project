package com.jml.reconciliation.service;

import com.jml.reconciliation.dto.RollbackRequest;
import com.jml.reconciliation.entity.Entitlement;
import com.jml.reconciliation.entity.RemediationAction;
import com.jml.reconciliation.model.enums.PermissionLevel;
import com.jml.reconciliation.model.enums.RemediationStatus;
import com.jml.reconciliation.repository.DirectoryGroupRepository;
import com.jml.reconciliation.repository.EntitlementRepository;
import com.jml.reconciliation.repository.RemediationActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RemediationService {

    private final RemediationActionRepository actionRepository;
    private final EntitlementRepository entitlementRepository;
    private final DirectoryGroupRepository directoryGroupRepository;
    private final AuditService auditService;

    public RemediationService(
            RemediationActionRepository actionRepository,
            EntitlementRepository entitlementRepository,
            DirectoryGroupRepository directoryGroupRepository,
            AuditService auditService) {
        this.actionRepository = actionRepository;
        this.entitlementRepository = entitlementRepository;
        this.directoryGroupRepository = directoryGroupRepository;
        this.auditService = auditService;
    }

    public List<RemediationAction> getAllActions() {
        return actionRepository.findAll();
    }

    @Transactional
    public RemediationAction executeAction(Long actionId) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation action not found with ID: " + actionId));

        if (action.getStatus() != RemediationStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED actions can be executed. Current status: " + action.getStatus());
        }

        if (action.getAppName() != null && action.getAppName().startsWith("Directory Group:")) {
            directoryGroupRepository.deleteByUsername(action.getUsername());
        } else if (action.getAppName() != null) {
            Entitlement entitlement = entitlementRepository.findByUsernameAndAppName(action.getUsername(), action.getAppName())
                    .orElse(null);
            if (entitlement != null) {
                action.setPreviousPermissionLevel(entitlement.getPermissionLevel());
                if (action.getTargetPermissionLevel() == PermissionLevel.NONE) {
                    entitlementRepository.delete(entitlement);
                } else {
                    entitlement.setPermissionLevel(action.getTargetPermissionLevel());
                    entitlementRepository.save(entitlement);
                }
            }
        }

        action.setStatus(RemediationStatus.EXECUTED);
        action.setExecutedAt(LocalDateTime.now());
        RemediationAction updated = actionRepository.save(action);

        auditService.logEvent("REMEDIATION_EXECUTED", "SYSTEM", action.getUsername(),
                String.format("Executed remediation action %s for %s on %s. Target permission set to %s.",
                        action.getActionType(), action.getFullName(), action.getAppName(), action.getTargetPermissionLevel()));

        return updated;
    }

    @Transactional
    public RemediationAction rollbackAction(Long actionId, RollbackRequest request) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation action not found with ID: " + actionId));

        if (action.getStatus() != RemediationStatus.EXECUTED) {
            throw new IllegalStateException("Only EXECUTED actions can be rolled back. Current status: " + action.getStatus());
        }

        PermissionLevel levelToRestore = action.getPreviousPermissionLevel() != null ?
                action.getPreviousPermissionLevel() : PermissionLevel.ADMIN;

        if (action.getAppName() != null && !action.getAppName().startsWith("Directory Group:")) {
            Entitlement entitlement = entitlementRepository.findByUsernameAndAppName(action.getUsername(), action.getAppName())
                    .orElse(new Entitlement(action.getUsername(), action.getAppName(), levelToRestore));
            entitlement.setPermissionLevel(levelToRestore);
            entitlementRepository.save(entitlement);
        }

        action.setStatus(RemediationStatus.ROLLED_BACK);
        action.setRolledBackAt(LocalDateTime.now());
        RemediationAction updated = actionRepository.save(action);

        String actor = (request != null && request.getActor() != null) ? request.getActor() : "ADMIN";
        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Manual rollback requested";

        auditService.logEvent("STATUS_ROLLED_BACK", actor, action.getUsername(),
                String.format("Rolled back remediation action #%d for %s on %s. Restored permission level to %s. Reason: %s",
                        actionId, action.getFullName(), action.getAppName(), levelToRestore, reason));

        return updated;
    }

    @Transactional
    public RemediationAction validateAction(Long actionId, String validatedBy, String notes) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation action not found with ID: " + actionId));

        action.setStakeholderValidated(true);
        action.setValidatedBy(validatedBy != null && !validatedBy.isBlank() ? validatedBy : "STAKEHOLDER_AUDITOR");
        action.setValidationNotes(notes != null ? notes : "Stakeholder verified evidence trail and confirmed compliance.");
        action.setValidatedAt(LocalDateTime.now());

        RemediationAction updated = actionRepository.save(action);

        auditService.logEvent("STAKEHOLDER_VALIDATED", updated.getValidatedBy(), action.getUsername(),
                String.format("Stakeholder %s validated evidence trail for remediation #%d (%s). Notes: %s",
                        updated.getValidatedBy(), actionId, action.getActionType(), updated.getValidationNotes()));

        return updated;
    }
}
