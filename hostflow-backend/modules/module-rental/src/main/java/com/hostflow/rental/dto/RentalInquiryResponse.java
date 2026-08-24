package com.hostflow.rental.dto;

import com.hostflow.rental.entity.RentalInquiry;

import java.time.Instant;
import java.util.UUID;

public record RentalInquiryResponse(UUID id, UUID propertyId, UUID guestUserId, String message, String status,
        String replyMessage, Instant repliedAt) {

    public static RentalInquiryResponse from(RentalInquiry inquiry) {
        return new RentalInquiryResponse(inquiry.getId(), inquiry.getPropertyId(), inquiry.getGuestUserId(),
                inquiry.getMessage(), inquiry.getStatus().name(), inquiry.getReplyMessage(), inquiry.getRepliedAt());
    }
}
