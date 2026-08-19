package com.hostflow.app.platformadmin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Item 15 (cross-product monitoring): basic usage counters across both products —
 * a genuinely minimal first version. Real monitoring (error rates, latency
 * percentiles, request volume over time) belongs in Prometheus/Grafana per the
 * original architecture decision, NOT reinvented here in application code — this
 * endpoint only covers simple business-metric counts that are naturally DB-backed
 * and don't need a full metrics pipeline.
 */
@Component
public class PlatformMonitoringQueries {

    private final JdbcTemplate jdbcTemplate;

    public PlatformMonitoringQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record MonitoringSnapshot(long totalOrganizations, long totalXanuosUsers, long totalGuestProfiles,
                                      long totalActiveBookings, long totalOpenSupportTickets, long totalOpenWorkOrders) {
    }

    public MonitoringSnapshot snapshot() {
        long orgs = count("SELECT COUNT(*) FROM organizations");
        long users = count("SELECT COUNT(*) FROM users");
        long guests = count("SELECT COUNT(*) FROM guest_profiles");
        long activeBookings = count("SELECT COUNT(*) FROM bookings WHERE status IN ('PENDING','CONFIRMED','CHECKED_IN')");
        long openTickets = count("SELECT COUNT(*) FROM crm_support_tickets WHERE status IN ('OPEN','IN_PROGRESS')");
        long openWorkOrders = count("SELECT COUNT(*) FROM maintenance_work_orders WHERE status IN ('OPEN','ASSIGNED','IN_PROGRESS')");

        return new MonitoringSnapshot(orgs, users, guests, activeBookings, openTickets, openWorkOrders);
    }

    private long count(String sql) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class);
        return result != null ? result : 0L;
    }
}
