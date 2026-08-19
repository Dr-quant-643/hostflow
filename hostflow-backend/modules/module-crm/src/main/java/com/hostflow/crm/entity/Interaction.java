package com.hostflow.crm.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Append-only log — no update/delete methods exposed, intentionally. An
 * interaction history is a factual record of what happened; correcting a mistaken
 * entry should be done by adding a new corrective interaction, not editing history,
 * mirroring standard CRM/audit-log conventions.
 */
@Entity
@Table(name = "crm_interactions")
public class Interaction extends TenantScopedEntity {

    @Column(name = "contact_id", nullable = false)
    private UUID contactId;

    @Column(name = "logged_by_user_id", nullable = false)
    private UUID loggedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InteractionType type;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    protected Interaction() {
    }

    public Interaction(UUID contactId, UUID loggedByUserId, InteractionType type, String notes) {
        this.contactId = contactId;
        this.loggedByUserId = loggedByUserId;
        this.type = type;
        this.notes = notes;
    }

    public UUID getContactId() {
        return contactId;
    }

    public UUID getLoggedByUserId() {
        return loggedByUserId;
    }

    public InteractionType getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }
}
