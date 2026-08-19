package com.hostflow.app.publicapi;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.review.dto.CreateReviewRequest;
import com.hostflow.review.dto.ReviewResponse;
import com.hostflow.review.entity.Review;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Same pattern as GuestBookingOrchestrator: resolves the booking's property/tenant
 * via cross-tenant lookup, verifies the caller actually owns the booking AND that
 * it's in a completed state (only CHECKED_OUT bookings can be reviewed — reviewing
 * before a stay happened makes no sense), sets TenantContext to the property's
 * tenant, delegates the actual write to a separate bean, clears context after.
 */
@Component
public class GuestReviewOrchestrator {

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final GuestReviewWriter writer;

    public GuestReviewOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
                                    GuestReviewWriter writer) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.writer = writer;
    }

    public ReviewResponse submitReview(UUID reviewerUserId, CreateReviewRequest request) {
        BookingInfo bookingInfo = resolveAndVerifyBooking(request.bookingId(), reviewerUserId);

        TenantContext.set(bookingInfo.tenantId());
        try {
            Review review = writer.create(bookingInfo.propertyId(), request.bookingId(), reviewerUserId,
                    request.rating(), request.comment());
            return ReviewResponse.from(review);
        } finally {
            TenantContext.clear();
        }
    }

    private record BookingInfo(UUID tenantId, UUID propertyId) {
    }

    private BookingInfo resolveAndVerifyBooking(UUID bookingId, UUID reviewerUserId) {
        String sql = "SELECT tenant_id, property_id, guest_user_id, status FROM bookings WHERE id = ?";
        List<Object[]> results = platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
                rs.getString("tenant_id"), rs.getString("property_id"), rs.getString("guest_user_id"), rs.getString("status")
        }, bookingId);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Booking", bookingId);
        }
        Object[] row = results.get(0);
        UUID actualGuestId = UUID.fromString((String) row[2]);
        String status = (String) row[3];

        if (!actualGuestId.equals(reviewerUserId)) {
            throw new BusinessRuleException("You can only review your own bookings");
        }
        if (!"CHECKED_OUT".equals(status)) {
            throw new BusinessRuleException("You can only review a completed (CHECKED_OUT) booking");
        }

        return new BookingInfo(UUID.fromString((String) row[0]), UUID.fromString((String) row[1]));
    }
}
