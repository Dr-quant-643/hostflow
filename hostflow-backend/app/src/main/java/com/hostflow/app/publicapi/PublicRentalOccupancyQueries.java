package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Guest-facing occupancy signal for MONTHLY-classified properties, which have
 * no self-service Booking flow (see RentalModel's javadoc) -- "available"
 * here means "no active lease covers today", read directly off module-rental's
 * leases table via the same cross-tenant platformAdminJdbcTemplate pattern as
 * PublicAvailabilityQueries/PublicPropertyQueries. Deliberately NOT reusing
 * module-analytics's mv_property_occupancy_summary: that view is an all-time
 * total refreshed periodically (a staff-facing rollup), not a real-time
 * signal suitable for a guest deciding whether to inquire right now.
 */
@Component
public class PublicRentalOccupancyQueries {

    private final JdbcTemplate jdbcTemplate;

    public PublicRentalOccupancyQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record OccupancyResult(boolean occupied, int occupancyRatePercent) {
    }

    public OccupancyResult forProperty(UUID propertyId) {
        return new OccupancyResult(isCurrentlyLeased(propertyId), occupancyRatePercent(propertyId));
    }

    private boolean isCurrentlyLeased(UUID propertyId) {
        String sql = """
                SELECT COUNT(*) FROM leases
                WHERE property_id = ?
                  AND status = 'ACTIVE'
                  AND start_date <= CURRENT_DATE
                  AND end_date >= CURRENT_DATE
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, propertyId);
        return count != null && count > 0;
    }

    /**
     * Sum of each lease's date range (ACTIVE/EXPIRED/TERMINATED -- a DRAFT
     * lease never actually housed anyone), clipped to
     * [property created_at, today], divided by days elapsed since the
     * property was created. A plain SQL aggregate, computed live -- not a
     * materialized view, since a guest deciding whether to inquire needs a
     * current number, not one that's stale until the next periodic refresh.
     */
    private int occupancyRatePercent(UUID propertyId) {
        String sql = """
                SELECT
                    COALESCE(SUM(
                        GREATEST(0, LEAST(l.end_date, CURRENT_DATE) - GREATEST(l.start_date, p.created_at::date))
                    ), 0) AS leased_days,
                    GREATEST(1, CURRENT_DATE - p.created_at::date) AS elapsed_days
                FROM properties p
                LEFT JOIN leases l ON l.property_id = p.id AND l.status IN ('ACTIVE', 'EXPIRED', 'TERMINATED')
                WHERE p.id = ?
                GROUP BY p.created_at
                """;
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return 0;
            }
            long leasedDays = rs.getLong("leased_days");
            long elapsedDays = rs.getLong("elapsed_days");
            return (int) Math.round(100.0 * leasedDays / elapsedDays);
        }, propertyId);
    }
}
