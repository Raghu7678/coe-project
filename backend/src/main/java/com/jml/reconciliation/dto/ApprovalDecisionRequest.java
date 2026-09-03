package com.jml.reconciliation.dto;

import jakarta.validation.constraints.NotBlank;

public class ApprovalDecisionRequest {

    @NotBlank(message = "Reviewer ID is required")
    private String reviewerId;

    @NotBlank(message = "Decision must be APPROVE or REJECT")
    private String decision;

    private String comment;

    public ApprovalDecisionRequest() {}

    public ApprovalDecisionRequest(String reviewerId, String decision, String comment) {
        this.reviewerId = reviewerId;
        this.decision = decision;
        this.comment = comment;
    }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
