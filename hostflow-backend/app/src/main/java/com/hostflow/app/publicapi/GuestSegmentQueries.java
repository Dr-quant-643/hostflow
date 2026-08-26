package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The guest/tenant segmentation engine -- an RFM-lite model (Recency,
 * Frequency, Monetary) computed live over this owner's own bookings + leases,
 * not a new event-tracking pipeline. A "customer" here means a NazilCo
 * guest/tenant of THIS owner's properties specifically, not a global
 * cross-tenant guest profile -- consistent with every other owner-facing
 * aggregate in this codebase being scoped to "my properties," and it avoids
 * building a cross-tenant guest-profile system with its own privacy surface.
 *
 * Deliberately LIVE, not a materialized view: same reasoning as
 * PublicRentalOccupancyQueries -- an owner deciding how to treat a guest
 * needs a current number, not one that's stale until AnalyticsRefreshJob's
 * next daily 2 AM run.
 *
 * Same platformAdminJdbcTemplate + explicit owner_user_id filter as
 * RentalInquiryOrchestrator.myInquiriesAsOwner / OwnerWorkOrderQueries --
 * proven pattern for "owner viewing aggregate data across their properties."
 */
@Component
public class GuestSegmentQueries {

    private final JdbcTemplate platformAdminJdbcTemplate;

    public GuestSegmentQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public record GuestSegmentRow(UUID guestUserId, String name, String email, int totalBookings,
            int totalReservations, BigDecimal totalSpend, LocalDate firstActivityDate,
            LocalDate lastActivityDate, Integer recencyDays, boolean hasActiveLease, String segment) {
    }

    /**
     * Segment assignment, in priority order:
     *  ACTIVE_TENANT  - currently has an ACTIVE lease with this owner
     *  AT_RISK        - nothing in the last 180 days (checked before VIP/REPEAT
     *                    deliberately -- a big spender who went quiet 2 years
     *                    ago is more usefully flagged as lapsed than as VIP;
     *                    recency dominates frequency/monetary in RFM)
     *  VIP            - top 10% of this owner's guests by total spend, with 2+ stays
     *  REPEAT         - 2+ bookings/reservations
     *  NEW            - exactly one stay, recent
     * A guest can only reach this query at all via a real booking/lease, so
     * there is no "zero stays" case to classify.
     */
    public List<GuestSegmentRow> segmentsForOwner(UUID ownerUserId) {
        return segments("p.owner_user_id = ?", ownerUserId);
    }

    /** Same segmentation, scoped to every property in a tenant rather than
     *  one owner's -- a tenant/org can have multiple owner users, so this is
     *  what the public API (authenticated by an org-level ApiKey, not a
     *  specific user) needs instead of segmentsForOwner. */
    public List<GuestSegmentRow> segmentsForTenant(UUID tenantId) {
        return segments("p.tenant_id = ?", tenantId);
    }

    private List<GuestSegmentRow> segments(String propertyFilter, UUID filterValue) {
        String sql = """
                WITH guest_bookings AS (
                    SELECT b.guest_user_id,
                           COUNT(*) FILTER (WHERE b.status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')) AS booking_count,
                           COALESCE(SUM(b.total_price) FILTER (WHERE b.status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')), 0) AS booking_spend,
                           MAX(b.check_out) FILTER (WHERE b.status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')) AS last_booking_date,
                           MIN(b.check_in) FILTER (WHERE b.status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')) AS first_booking_date
                    FROM bookings b
                    JOIN properties p ON p.id = b.property_id
                    WHERE %1$s
                    GROUP BY b.guest_user_id
                ),
                guest_leases AS (
                    SELECT rt.linked_user_id AS guest_user_id,
                           COUNT(*) FILTER (WHERE l.status IN ('ACTIVE','EXPIRED','TERMINATED')) AS lease_count,
                           COALESCE(SUM(l.monthly_rent * GREATEST(1,
                               CEIL((LEAST(l.end_date, CURRENT_DATE) - l.start_date) / 30.0)
                           )) FILTER (WHERE l.status IN ('ACTIVE','EXPIRED','TERMINATED')), 0) AS lease_spend,
                           MAX(l.end_date) FILTER (WHERE l.status IN ('ACTIVE','EXPIRED','TERMINATED')) AS last_lease_date,
                           MIN(l.start_date) FILTER (WHERE l.status IN ('ACTIVE','EXPIRED','TERMINATED')) AS first_lease_date,
                           BOOL_OR(l.status = 'ACTIVE') AS has_active_lease
                    FROM leases l
                    JOIN rental_tenants rt ON rt.id = l.tenant_id_ref
                    JOIN properties p ON p.id = l.property_id
                    WHERE %1$s AND rt.linked_user_id IS NOT NULL
                    GROUP BY rt.linked_user_id
                ),
                combined AS (
                    SELECT
                        COALESCE(gb.guest_user_id, gl.guest_user_id) AS guest_user_id,
                        COALESCE(gb.booking_count, 0) AS total_bookings,
                        COALESCE(gl.lease_count, 0) AS total_reservations,
                        COALESCE(gb.booking_spend, 0) + COALESCE(gl.lease_spend, 0) AS total_spend,
                        LEAST(gb.first_booking_date, gl.first_lease_date) AS first_activity_date,
                        GREATEST(gb.last_booking_date, gl.last_lease_date) AS last_activity_date,
                        COALESCE(gl.has_active_lease, false) AS has_active_lease
                    FROM guest_bookings gb
                    FULL OUTER JOIN guest_leases gl ON gl.guest_user_id = gb.guest_user_id
                ),
                ranked AS (
                    SELECT c.*,
                           PERCENT_RANK() OVER (ORDER BY c.total_spend) AS spend_percentile
                    FROM combined c
                    WHERE c.total_bookings > 0 OR c.total_reservations > 0
                )
                SELECT
                    r.guest_user_id, r.total_bookings, r.total_reservations, r.total_spend,
                    r.first_activity_date, r.last_activity_date, r.has_active_lease,
                    (CURRENT_DATE - r.last_activity_date) AS recency_days,
                    CASE
                        WHEN r.has_active_lease THEN 'ACTIVE_TENANT'
                        WHEN (CURRENT_DATE - r.last_activity_date) > 180 THEN 'AT_RISK'
                        WHEN r.spend_percentile >= 0.9 AND (r.total_bookings + r.total_reservations) >= 2 THEN 'VIP'
                        WHEN (r.total_bookings + r.total_reservations) >= 2 THEN 'REPEAT'
                        ELSE 'NEW'
                    END AS segment
                FROM ranked r
                ORDER BY r.total_spend DESC
                """.formatted(propertyFilter);
        List<GuestSegmentRow> rows = platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> {
            UUID guestUserId = UUID.fromString(rs.getString("guest_user_id"));
            return new GuestSegmentRow(guestUserId, null, null,
                    rs.getInt("total_bookings"), rs.getInt("total_reservations"), rs.getBigDecimal("total_spend"),
                    rs.getDate("first_activity_date") != null ? rs.getDate("first_activity_date").toLocalDate() : null,
                    rs.getDate("last_activity_date") != null ? rs.getDate("last_activity_date").toLocalDate() : null,
                    (Integer) rs.getObject("recency_days"), rs.getBoolean("has_active_lease"),
                    rs.getString("segment"));
        }, filterValue, filterValue);

        return rows.stream().map(this::withIdentity).toList();
    }

    private GuestSegmentRow withIdentity(GuestSegmentRow row) {
        String[] identity = resolveNameAndEmail(row.guestUserId());
        return new GuestSegmentRow(row.guestUserId(), identity[0], identity[1], row.totalBookings(),
                row.totalReservations(), row.totalSpend(), row.firstActivityDate(), row.lastActivityDate(),
                row.recencyDays(), row.hasActiveLease(), row.segment());
    }

    /** Guest identity can live in guest_profiles OR users (a rental tenant
     *  linked via a staff/owner-created account) -- same fallback order as
     *  DomainAuditEventConsumer.resolveGuestEmail. */
    private String[] resolveNameAndEmail(UUID userId) {
        List<String[]> guestRows = platformAdminJdbcTemplate.query(
                "SELECT first_name, last_name, email FROM guest_profiles WHERE keycloak_id = ?",
                (rs, rowNum) -> new String[] { rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("email") },
                userId.toString());
        if (!guestRows.isEmpty()) {
            return guestRows.get(0);
        }
        List<String[]> userRows = platformAdminJdbcTemplate.query(
                "SELECT first_name, last_name, email FROM users WHERE keycloak_id = ?",
                (rs, rowNum) -> new String[] { rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("email") },
                userId.toString());
        return userRows.isEmpty() ? new String[] { "Unknown guest", null } : userRows.get(0);
    }
}
