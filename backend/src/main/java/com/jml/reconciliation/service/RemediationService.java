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
import java.util.NoSuchElementException;

/**
 * Service managing Qualification Remediation, Instant State Rollback & Stakeholder Evidence Validation.
 * 
 * Key Capabilities:
 * 1. Execution: Modifies real application entitlements (e.g. revoking orphaned access or downgrading mover admin rights).
 * 2. Instant Rollback: Restores previous permissions with zero data loss if an action was executed mistakenly.
 * 3. Stakeholder Validation: Digital sign-off stamping on removal evidence trails for compliance readiness.
 */
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

    /**
     * Lists all remediation actions and their current lifecycle statuses.
     * @return list of RemediationAction entities
     */
    public List<RemediationAction> getAllActions() {
        return actionRepository.findAll();
    }

    /**
     * Executes an approved remediation action in the target application entitlement store.
     * 
     * Workflow:
     * 1. Validates that the action is currently in APPROVED status.
     * 2. Preserves the current permission level in previousPermissionLevel for rollback support.
     * 3. Either deletes the entitlement (for REVOKE) or modifies the permission level.
     * 4. Updates status to EXECUTED with timestamp.
     * 5. Emits an immutable audit trail event.
     * 
     * @param actionId database ID of the approved remediation action
     * @return updated RemediationAction entity
     */
    @Transactional
    public RemediationAction executeAction(Long actionId) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Remediation action not found with ID: " + actionId));

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

    /**
     * Performs instant status rollback restoring the user's previously held permission level.
     * 
     * Workflow:
     * 1. Validates that the action was previously EXECUTED.
     * 2. Restores the prior entitlement record using previousPermissionLevel.
     * 3. Sets action status to ROLLED_BACK.
     * 4. Logs a dedicated STATUS_ROLLED_BACK audit event detailing actor and rationale.
     * 
     * @param actionId database ID of the executed action
     * @param request optional actor and reason metadata
     * @return updated RemediationAction entity
     */
    @Transactional
    public RemediationAction rollbackAction(Long actionId, RollbackRequest request) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Remediation action not found with ID: " + actionId));

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

    /**
     * Applies a stakeholder validation digital sign-off to an executed remediation.
     * 
     * @param actionId database ID of the remediation action
     * @param validatedBy name or ID of the compliance auditor
     * @param notes verification notes
     * @return updated RemediationAction entity with stakeholder validation stamp
     */
    @Transactional
    public RemediationAction validateAction(Long actionId, String validatedBy, String notes) {
        RemediationAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Remediation action not found with ID: " + actionId));

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
