package com.jml.reconciliation.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType; // RECONCILIATION_RUN, SAFETY_GATE_TRIGGERED, APPROVAL_GRANTED, APPROVAL_REJECTED, REMEDIATION_EXECUTED, STATUS_ROLLED_BACK

    private String actor;

    private String targetEntity;

    @Column(length = 2000)
    private String details;

    private LocalDateTime timestamp;

    public AuditEvent() {}

    public AuditEvent(String eventType, String actor, String targetEntity, String details) {
        this.eventType = eventType;
        this.actor = actor;
        this.targetEntity = targetEntity;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getTargetEntity() { return targetEntity; }
    public void setTargetEntity(String targetEntity) { this.targetEntity = targetEntity; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
