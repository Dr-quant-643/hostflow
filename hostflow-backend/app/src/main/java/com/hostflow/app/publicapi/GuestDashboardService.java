package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Aggregates a guest's bookings, invoices, and notification logs into one response
 * — the individual pieces already exist as separate endpoints
 * (/bookings/public/mine, etc.), this is purely a convenience aggregation for a
 * single dashboard screen, reading via platformAdminJdbcTemplate for the same
 * reason every other guest-facing query does (no tenant context for an anonymous/
 * tenant-less guest identity).
 */
@Component
public class GuestDashboardService {

    private final JdbcTemplate jdbcTemplate;

    public GuestDashboardService(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record DashboardBookingRow(UUID id, UUID propertyId, LocalDate checkIn, LocalDate checkOut, String status) {
    }

    public record DashboardInvoiceRow(UUID id, BigDecimal amount, LocalDate dueDate, String status) {
    }

    public record DashboardNotificationRow(UUID id, String templateCode, String channel, String status, Instant sentAt) {
    }

    public record GuestDashboardResponse(List<DashboardBookingRow> upcomingBookings,
                                          List<DashboardInvoiceRow> recentInvoices,
                                          List<DashboardNotificationRow> recentNotifications) {
    }

    public GuestDashboardResponse buildDashboard(UUID guestUserId) {
        List<DashboardBookingRow> bookings = jdbcTemplate.query(
                """
                SELECT id, property_id, check_in, check_out, status FROM bookings
                WHERE guest_user_id = ? AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                ORDER BY check_in ASC LIMIT 10
                """,
                (rs, rowNum) -> new DashboardBookingRow(
                        UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("property_id")),
                        rs.getDate("check_in").toLocalDate(), rs.getDate("check_out").toLocalDate(), rs.getString("status")),
                guestUserId);

        List<DashboardInvoiceRow> invoices = jdbcTemplate.query(
                """
                SELECT id, amount, due_date, status FROM invoices
                WHERE billed_user_id = ? ORDER BY due_date DESC LIMIT 10
                """,
                (rs, rowNum) -> new DashboardInvoiceRow(
                        UUID.fromString(rs.getString("id")), rs.getBigDecimal("amount"),
                        rs.getDate("due_date").toLocalDate(), rs.getString("status")),
                guestUserId);

        List<DashboardNotificationRow> notifications = jdbcTemplate.query(
                """
                SELECT id, template_code, channel, status, sent_at FROM notification_logs
                WHERE recipient_user_id = ? ORDER BY created_at DESC LIMIT 10
                """,
                (rs, rowNum) -> new DashboardNotificationRow(
                        UUID.fromString(rs.getString("id")), rs.getString("template_code"), rs.getString("channel"),
                        rs.getString("status"), rs.getTimestamp("sent_at") != null ? rs.getTimestamp("sent_at").toInstant() : null),
                guestUserId);

        return new GuestDashboardResponse(bookings, invoices, notifications);
    }
}
