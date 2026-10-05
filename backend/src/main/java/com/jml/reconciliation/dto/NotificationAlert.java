package com.jml.reconciliation.dto;

import java.time.LocalDateTime;

public class NotificationAlert {
    private String id;
    private String title;
    private String message;
    private String severity; // CRITICAL, WARNING, INFO
    private String targetTab; // assessment, approvals, remediation, audit, sources
    private Long referenceId;
    private LocalDateTime timestamp;
    private boolean read;

    public NotificationAlert() {}

    public NotificationAlert(String id, String title, String message, String severity, String targetTab, Long referenceId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.targetTab = targetTab;
        this.referenceId = referenceId;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getTargetTab() { return targetTab; }
    public void setTargetTab(String targetTab) { this.targetTab = targetTab; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
