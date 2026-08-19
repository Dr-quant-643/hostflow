package com.hostflow.app.platformadmin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Backs nazilco-admin's Bookings Oversight screen (/admin/bookings). */
@Component
public class PlatformBookingQueries {

    private final JdbcTemplate jdbcTemplate;

    public PlatformBookingQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record BookingOversightRow(UUID id, UUID tenantId, String organizationName, UUID propertyId,
            LocalDate checkIn, LocalDate checkOut, String status) {
    }

    private static final RowMapper<BookingOversightRow> ROW_MAPPER = (rs, rowNum) -> new BookingOversightRow(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("tenant_id")),
            rs.getString("organization_name"),
            UUID.fromString(rs.getString("property_id")),
            rs.getDate("check_in").toLocalDate(),
            rs.getDate("check_out").toLocalDate(),
            rs.getString("status"));

    public List<BookingOversightRow> findAllAcrossTenants(String statusFilter, int limit, int offset) {
        String sql = """
                SELECT b.id, b.tenant_id, o.name AS organization_name, b.property_id,
                       b.check_in, b.check_out, b.status
                FROM bookings b
                JOIN organizations o ON o.id = b.tenant_id
                WHERE (?::text IS NULL OR b.status = ?)
                ORDER BY b.check_in DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, statusFilter, statusFilter, limit, offset);
    }
}
