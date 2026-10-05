package com.jml.reconciliation.dto;

public class RollbackRequest {

    private String actor;
    private String reason;

    public RollbackRequest() {}

    public RollbackRequest(String actor, String reason) {
        this.actor = actor;
        this.reason = reason;
    }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
