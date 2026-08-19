package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Serves a rental tenant viewing their OWN lease + rent schedule, resolved via
 * RentalTenant.linked_user_id. Cross-tenant lookup (platformAdminJdbcTemplate)
 * since a linked rental tenant may hold PRODUCT_NAZILCO with no tenant_id, same
 * situation as every other guest-facing query.
 */
@Component
public class RentalPortalQueries {

    private final JdbcTemplate jdbcTemplate;

    public RentalPortalQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record MyLeaseRow(UUID id, UUID propertyId, LocalDate startDate, LocalDate endDate,
                              BigDecimal monthlyRent, String status) {
    }

    public record MyRentPaymentRow(UUID id, LocalDate dueDate, BigDecimal amount, String status, LocalDate paidDate) {
    }

    public List<MyLeaseRow> myLeases(UUID linkedUserId) {
        String sql = """
                SELECT l.id, l.property_id, l.start_date, l.end_date, l.monthly_rent, l.status
                FROM leases l
                JOIN rental_tenants rt ON rt.id = l.tenant_id_ref
                WHERE rt.linked_user_id = ?
                ORDER BY l.start_date DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MyLeaseRow(
                UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("property_id")),
                rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                rs.getBigDecimal("monthly_rent"), rs.getString("status")), linkedUserId);
    }

    public List<MyRentPaymentRow> myRentSchedule(UUID linkedUserId, UUID leaseId) {
        String sql = """
                SELECT rp.id, rp.due_date, rp.amount, rp.status, rp.paid_date
                FROM rent_payments rp
                JOIN leases l ON l.id = rp.lease_id
                JOIN rental_tenants rt ON rt.id = l.tenant_id_ref
                WHERE rt.linked_user_id = ? AND rp.lease_id = ?
                ORDER BY rp.due_date ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MyRentPaymentRow(
                UUID.fromString(rs.getString("id")), rs.getDate("due_date").toLocalDate(),
                rs.getBigDecimal("amount"), rs.getString("status"),
                rs.getDate("paid_date") != null ? rs.getDate("paid_date").toLocalDate() : null),
                linkedUserId, leaseId);
    }
}
