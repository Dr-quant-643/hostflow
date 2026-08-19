package com.hostflow.crm.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "crm_support_tickets")
public class SupportTicket extends TenantScopedEntity {

    @Column(name = "contact_id")
    private UUID contactId;

    @Column(name = "raised_by_user_id")
    private UUID raisedByUserId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_scope", nullable = false)
    private TicketProductScope productScope;

    @Column(name = "assigned_to_user_id")
    private UUID assignedToUserId;

    protected SupportTicket() {
    }

    public SupportTicket(UUID contactId, UUID raisedByUserId, String subject, String description,
            TicketPriority priority, TicketProductScope productScope) {
        this.contactId = contactId;
        this.raisedByUserId = raisedByUserId;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.productScope = productScope;
        this.status = TicketStatus.OPEN;
    }

    public void assign(UUID staffUserId) {
        if (status == TicketStatus.CLOSED) {
            throw new BusinessRuleException("Cannot assign a CLOSED ticket");
        }
        this.assignedToUserId = staffUserId;
        if (status == TicketStatus.OPEN) {
            this.status = TicketStatus.IN_PROGRESS;
        }
    }

    public void resolve() {
        if (status != TicketStatus.IN_PROGRESS && status != TicketStatus.OPEN) {
            throw new BusinessRuleException("Cannot resolve a ticket with status " + status);
        }
        this.status = TicketStatus.RESOLVED;
    }

    public void reopen() {
        if (status != TicketStatus.RESOLVED) {
            throw new BusinessRuleException("Cannot reopen a ticket with status " + status + " (expected RESOLVED)");
        }
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void close() {
        if (status != TicketStatus.RESOLVED) {
            throw new BusinessRuleException("Cannot close a ticket with status " + status + " (expected RESOLVED)");
        }
        this.status = TicketStatus.CLOSED;
    }

    public UUID getContactId() {
        return contactId;
    }

    public UUID getRaisedByUserId() {
        return raisedByUserId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketProductScope getProductScope() {
        return productScope;
    }

    public UUID getAssignedToUserId() {
        return assignedToUserId;
    }
}
