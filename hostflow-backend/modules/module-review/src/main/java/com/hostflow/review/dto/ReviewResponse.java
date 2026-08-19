package com.hostflow.review.dto;

import com.hostflow.review.entity.Review;

import java.util.UUID;

public record ReviewResponse(UUID id, UUID propertyId, UUID bookingId, Integer rating, String comment, String ownerResponse) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(r.getId(), r.getPropertyId(), r.getBookingId(), r.getRating(), r.getComment(), r.getOwnerResponse());
    }
}
