package com.hostflow.app.publicapi;

import com.hostflow.booking.dto.BookingResponse;
import com.hostflow.booking.dto.CreateBookingRequest;
import com.hostflow.booking.entity.Booking;
import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * THE fix for the tenant-resolution gap: a guest has no tenant of their own, so
 * this class resolves the booking's tenant from the PROPERTY being booked (via
 * a
 * cross-tenant platformAdminJdbcTemplate lookup), explicitly sets TenantContext
 * to
 * that value BEFORE calling into GuestBookingWriter's @Transactional methods
 * (which
 * is required — TenantAwareJpaTransactionManager reads TenantContext at
 * transaction
 * start, so it must already be set by the time the transactional method
 * begins),
 * and clears it in a finally block afterward — same discipline used everywhere
 * else
 * TenantContext is touched in this codebase.
 */
@Component
public class GuestBookingOrchestrator {

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final GuestBookingWriter writer;

    public GuestBookingOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            GuestBookingWriter writer) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.writer = writer;
    }

    public BookingResponse createBooking(UUID guestUserId, CreateBookingRequest request) {
        UUID propertyTenantId = resolvePropertyTenant(request.propertyId());

        TenantContext.set(propertyTenantId);
        try {
            Booking booking = writer.create(guestUserId, request);
            return BookingResponse.from(booking);
        } finally {
            TenantContext.clear();
        }
    }

    public BookingResponse cancelBooking(UUID bookingId, UUID guestUserId) {
        UUID bookingTenantId = resolveBookingTenantAndVerifyOwnership(bookingId, guestUserId);

        TenantContext.set(bookingTenantId);
        try {
            Booking booking = writer.cancel(bookingId);
            return BookingResponse.from(booking);
        } finally {
            TenantContext.clear();
        }
    }

    public List<BookingResponse> myBookings(UUID guestUserId) {
        String sql = """
                SELECT id, property_id, guest_user_id, check_in, check_out, status, total_price, decline_reason
                FROM bookings WHERE guest_user_id = ? ORDER BY check_in DESC
                """;
        return platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> new BookingResponse(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("property_id")),
                UUID.fromString(rs.getString("guest_user_id")),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getString("status"),
                rs.getBigDecimal("total_price"),
                rs.getString("decline_reason")), guestUserId);
    }

    private UUID resolvePropertyTenant(UUID propertyId) {
        String sql = "SELECT tenant_id FROM properties WHERE id = ? AND status = 'ACTIVE'";
        List<UUID> results = platformAdminJdbcTemplate.query(sql,
                (rs, rowNum) -> UUID.fromString(rs.getString("tenant_id")), propertyId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        return results.get(0);
    }

    /**
     * Ownership check happens HERE, in application code — since these operations
     * bypass RLS (platformAdminJdbcTemplate), Postgres itself does not enforce
     * "only the guest who made this booking can confirm/cancel it." This explicit
     * check is the ONLY enforcement of that rule, same pattern as
     * AnalyticsService's explicit-filtering-is-primary-not-secondary discipline.
     */
    private UUID resolveBookingTenantAndVerifyOwnership(UUID bookingId, UUID guestUserId) {
        String sql = "SELECT tenant_id, guest_user_id FROM bookings WHERE id = ?";
        List<Object[]> results = platformAdminJdbcTemplate.query(sql,
                (rs, rowNum) -> new Object[] { rs.getString("tenant_id"), rs.getString("guest_user_id") }, bookingId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Booking", bookingId);
        }
        Object[] row = results.get(0);
        UUID actualGuestId = UUID.fromString((String) row[1]);
        if (!actualGuestId.equals(guestUserId)) {
            throw new BusinessRuleException("This booking does not belong to the current user");
        }
        return UUID.fromString((String) row[0]);
    }
}
