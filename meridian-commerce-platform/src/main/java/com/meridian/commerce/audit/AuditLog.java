package com.meridian.commerce.audit;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actor;

    private String action;

    @Column(length = 4000)
    private String details;

    @Column(name = "occurred_at")
    private Instant occurredAt = Instant.now();

    public Long getId() { return id; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Instant getOccurredAt() { return occurredAt; }
}
