package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    public boolean isAvailable(UUID propertyId, LocalDate checkIn, LocalDate checkOut) {
        String sql = """
                SELECT COUNT(*) FROM bookings
                WHERE property_id = ?
                  AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                  AND check_in < ?
                  AND ? < check_out
                """;
        Integer overlapCount = jdbcTemplate.queryForObject(sql, Integer.class, propertyId, checkOut, checkIn);
        return overlapCount == null || overlapCount == 0;
    }
}
