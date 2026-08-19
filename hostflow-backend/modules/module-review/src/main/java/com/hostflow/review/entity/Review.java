package com.hostflow.review.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

/**
 * One review per (bookingId) — enforced at the DB level via a unique constraint,
 * not just in application code, so a guest can't submit multiple reviews for the
 * same stay even under a race condition. tenant_id here is resolved from the
 * PROPERTY being reviewed (same pattern as GuestBookingOrchestrator resolves
 * booking tenant from property) — reviews are cross-tenant-created by guests but
 * belong to the property owner's tenant for RLS purposes, since owners need to see
 * reviews of their own properties.
 */
@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = "booking_id"))
public class Review extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "reviewer_user_id", nullable = false)
    private UUID reviewerUserId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "owner_response", columnDefinition = "TEXT")
    private String ownerResponse;

    protected Review() {
    }

    public Review(UUID propertyId, UUID bookingId, UUID reviewerUserId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessRuleException("Rating must be between 1 and 5");
        }
        this.propertyId = propertyId;
        this.bookingId = bookingId;
        this.reviewerUserId = reviewerUserId;
        this.rating = rating;
        this.comment = comment;
    }

    public void addOwnerResponse(String response) {
        this.ownerResponse = response;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getReviewerUserId() {
        return reviewerUserId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getOwnerResponse() {
        return ownerResponse;
    }
}
