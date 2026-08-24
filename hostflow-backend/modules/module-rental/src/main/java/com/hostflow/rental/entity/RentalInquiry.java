package com.hostflow.rental.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * The persisted counterpart to what RentalInquiryOrchestrator used to do as
 * pure fire-and-forget: render a notification and forget the inquiry ever
 * existed. Tenant-scoped to the PROPERTY's tenant (same reasoning as
 * Booking/Lease) -- the guest sending the inquiry has no tenant of their
 * own. Deliberately a single guest message + single owner reply, not an
 * open-ended thread -- matches the actual ask (an owner can answer) rather
 * than building a general messaging system.
 */
@Entity
@Table(name = "rental_inquiries")
public class RentalInquiry extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "guest_user_id", nullable = false)
    private UUID guestUserId;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalInquiryStatus status;

    @Column(name = "reply_message")
    private String replyMessage;

    @Column(name = "replied_at")
    private Instant repliedAt;

    protected RentalInquiry() {
    }

    public RentalInquiry(UUID propertyId, UUID guestUserId, UUID ownerUserId, String message) {
        this.propertyId = propertyId;
        this.guestUserId = guestUserId;
        this.ownerUserId = ownerUserId;
        this.message = message;
        this.status = RentalInquiryStatus.OPEN;
    }

    public void reply(String replyMessage) {
        if (status != RentalInquiryStatus.OPEN) {
            throw new BusinessRuleException("This inquiry has already been replied to");
        }
        this.replyMessage = replyMessage;
        this.repliedAt = Instant.now();
        this.status = RentalInquiryStatus.REPLIED;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getGuestUserId() {
        return guestUserId;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public String getMessage() {
        return message;
    }

    public RentalInquiryStatus getStatus() {
        return status;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public Instant getRepliedAt() {
        return repliedAt;
    }
}
