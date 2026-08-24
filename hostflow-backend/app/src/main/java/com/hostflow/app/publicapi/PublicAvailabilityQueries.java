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
     * When unavailable, availableFrom is the latest check_out among the
     * overlapping bookings -- the day the property actually frees up, since
     * checkout dates are already known data that just wasn't surfaced before.
     */
    public AvailabilityResult checkAvailability(UUID propertyId, LocalDate checkIn, LocalDate checkOut) {
        String sql = """
                SELECT check_out FROM bookings
                WHERE property_id = ?
                  AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                  AND check_in < ?
                  AND ? < check_out
                ORDER BY check_out DESC
                LIMIT 1
                """;
        List<Date> overlapping = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getDate("check_out"), propertyId, checkOut, checkIn);
        if (overlapping.isEmpty()) {
            return new AvailabilityResult(true, null);
        }
        return new AvailabilityResult(false, overlapping.get(0).toLocalDate());
    }
}
