package com.hostflow.app.platformadmin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Backs hostflow-admin's Billing screen (/admin/billing/invoices). Every method
 * here is explicitly cross-tenant BY DESIGN — this is the deliberate exception
 * to
 * RLS, not an accident. Never add a method here that silently forgets to either
 * (a)
 * filter to a specific tenant, or (b) genuinely mean "all tenants" on purpose.
 */
@Component
public class PlatformBillingQueries {

    private final JdbcTemplate jdbcTemplate;

    public PlatformBillingQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record InvoiceSummaryRow(UUID id, UUID tenantId, String organizationName, BigDecimal amount,
            LocalDate dueDate, String status) {
    }

    private static final RowMapper<InvoiceSummaryRow> ROW_MAPPER = (rs, rowNum) -> new InvoiceSummaryRow(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("tenant_id")),
            rs.getString("organization_name"),
            rs.getBigDecimal("amount"),
            rs.getDate("due_date").toLocalDate(),
            rs.getString("status"));

    public List<InvoiceSummaryRow> findAllAcrossTenants(String statusFilter, int limit, int offset) {
        String sql = """
                SELECT i.id, i.tenant_id, o.name AS organization_name, i.amount, i.due_date, i.status
                FROM invoices i
                JOIN organizations o ON o.id = i.tenant_id
                WHERE (?::text IS NULL OR i.status = ?)
                ORDER BY i.due_date DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, statusFilter, statusFilter, limit, offset);
    }
}
