package com.hostflow.app.publicapi;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The "Personal Documents" capability from the NazilCo vision doc — a guest
 * viewing their own invoices/receipts. Ownership is enforced explicitly (same
 * pattern as GuestBookingOrchestrator's resolveBookingTenantAndVerifyOwnership),
 * since these queries bypass RLS via platformAdminJdbcTemplate.
 */
@Component
public class GuestInvoiceQueries {

    private final JdbcTemplate jdbcTemplate;

    public GuestInvoiceQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record GuestInvoiceRow(UUID id, BigDecimal amount, LocalDate dueDate, String status) {
    }

    public List<GuestInvoiceRow> listMine(UUID guestUserId) {
        return jdbcTemplate.query(
                "SELECT id, amount, due_date, status FROM invoices WHERE billed_user_id = ? ORDER BY due_date DESC",
                (rs, rowNum) -> new GuestInvoiceRow(UUID.fromString(rs.getString("id")), rs.getBigDecimal("amount"),
                        rs.getDate("due_date").toLocalDate(), rs.getString("status")),
                guestUserId);
    }

    public GuestInvoiceRow getMineById(UUID guestUserId, UUID invoiceId) {
        List<GuestInvoiceRow> results = jdbcTemplate.query(
                "SELECT id, amount, due_date, status, billed_user_id FROM invoices WHERE id = ?",
                (rs, rowNum) -> {
                    if (!rs.getString("billed_user_id").equals(guestUserId.toString())) {
                        throw new BusinessRuleException("This invoice does not belong to the current user");
                    }
                    return new GuestInvoiceRow(UUID.fromString(rs.getString("id")), rs.getBigDecimal("amount"),
                            rs.getDate("due_date").toLocalDate(), rs.getString("status"));
                }, invoiceId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Invoice", invoiceId);
        }
        return results.get(0);
    }
}
