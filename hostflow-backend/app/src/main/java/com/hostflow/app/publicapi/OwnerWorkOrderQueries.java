package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * THE fix for "I reported an issue but can't see it in the Maintenance tab" --
 * WorkOrderController.listByProperty requires a propertyId, and the
 * Maintenance page defaulted to whichever property happened to be first in
 * the owner's list, so a tenant-reported issue on a different property was
 * invisible unless the owner manually picked the right one. Mirrors
 * RentalInquiryOrchestrator.myInquiriesAsOwner exactly: platformAdminJdbcTemplate
 * plus an explicit owner_user_id filter (not RLS) is the enforcement here,
 * same reasoning as that class's own doc comment.
 */
@Component
public class OwnerWorkOrderQueries {

    private final JdbcTemplate platformAdminJdbcTemplate;

    public OwnerWorkOrderQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public record OwnerWorkOrderRow(UUID id, UUID propertyId, String propertyName, String category, String title,
            String description, String priority, String status, String resolutionNotes) {
    }

    public List<OwnerWorkOrderRow> mineAsOwner(UUID ownerUserId, int limit, int offset) {
        String sql = """
                SELECT w.id, w.property_id, p.name AS property_name, w.category, w.title, w.description,
                       w.priority, w.status, w.resolution_notes
                FROM maintenance_work_orders w
                JOIN properties p ON p.id = w.property_id
                WHERE p.owner_user_id = ?
                ORDER BY w.created_at DESC
                LIMIT ? OFFSET ?
                """;
        return platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> new OwnerWorkOrderRow(
                UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("property_id")),
                rs.getString("property_name"), rs.getString("category"), rs.getString("title"),
                rs.getString("description"), rs.getString("priority"), rs.getString("status"),
                rs.getString("resolution_notes")), ownerUserId, limit, offset);
    }
}
