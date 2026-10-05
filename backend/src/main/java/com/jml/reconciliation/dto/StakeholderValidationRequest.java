package com.jml.reconciliation.dto;

public class StakeholderValidationRequest {
    private String validatedBy;
    private String validationNotes;

    public StakeholderValidationRequest() {}

    public StakeholderValidationRequest(String validatedBy, String validationNotes) {
        this.validatedBy = validatedBy;
        this.validationNotes = validationNotes;
    }

    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }

    public String getValidationNotes() { return validationNotes; }
    public void setValidationNotes(String validationNotes) { this.validationNotes = validationNotes; }
}
