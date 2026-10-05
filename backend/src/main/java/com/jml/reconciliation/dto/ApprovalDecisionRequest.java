package com.jml.reconciliation.dto;

public class ApprovalDecisionRequest {

    private String reviewerId;
    private String decision; // APPROVED or REJECTED
    private String comments;

    public ApprovalDecisionRequest() {}

    public ApprovalDecisionRequest(String reviewerId, String decision, String comments) {
        this.reviewerId = reviewerId;
        this.decision = decision;
        this.comments = comments;
    }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
