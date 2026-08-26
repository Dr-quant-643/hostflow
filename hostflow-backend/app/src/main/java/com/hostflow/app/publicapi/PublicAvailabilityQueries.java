package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Backs the anonymous GET /api/v1/bookings/public/availability endpoint —
 * same cross-tenant, platformAdminJdbcTemplate reasoning as PublicPropertyQueries
 * (an anonymous NazilCo visitor checking a property's availability has no tenant
 * context to filter by). Mirrors BookingRepository.findOverlapping()'s half-open
 * interval logic and blocking-status set exactly, so "available" here means the
 * same thing it means at actual booking-creation time.
 */
@Component
public class PublicAvailabilityQueries {

    private final JdbcTemplate jdbcTemplate;

    public PublicAvailabilityQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record AvailabilityResult(boolean available, LocalDate availableFrom) {
    }

    /**
     * When unavailable, availableFrom is the latest check_out/end_date among
     * the overlapping bookings AND synced external-calendar blocks -- the
     * day the property actually frees up. Checking external_calendar_blocks
     * here (not just at booking-creation time in BookingAvailabilityService)
     * matters for UX: without it, a guest would see "Available" on this
     * check, then get rejected only when they actually try to book.
     */
    public AvailabilityResult checkAvailability(UUID propertyId, LocalDate checkIn, LocalDate checkOut) {
        String sql = """
                SELECT check_out AS blocked_until FROM bookings
                WHERE property_id = ?
                  AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                  AND check_in < ?
                  AND ? < check_out
                UNION ALL
                SELECT end_date AS blocked_until FROM external_calendar_blocks
                WHERE property_id = ?
                  AND start_date < ?
                  AND ? < end_date
                ORDER BY blocked_until DESC
                LIMIT 1
                """;
        List<Date> overlapping = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getDate("blocked_until"),
                propertyId, checkOut, checkIn, propertyId, checkOut, checkIn);
        if (overlapping.isEmpty()) {
            return new AvailabilityResult(true, null);
        }
        return new AvailabilityResult(false, overlapping.get(0).toLocalDate());
    }
}
