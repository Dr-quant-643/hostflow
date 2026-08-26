package com.hostflow.app.publicapi;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.maintenance.dto.CreateWorkOrderRequest;
import com.hostflow.maintenance.entity.WorkOrder;
import com.hostflow.maintenance.entity.WorkOrderPriority;
import com.hostflow.maintenance.service.WorkOrderService;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.service.NotificationService;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * THE fix for the "no connection between NazilCo and XanuOS for maintenance"
 * gap -- module-maintenance's WorkOrderController is entirely PRODUCT_XANUOS,
 * so a guest/tenant had no way to report an issue that ever reached the
 * owner. Mirrors RentalInquiryOrchestrator exactly: resolve the property's
 * tenant + owner via the cross-tenant platformAdminJdbcTemplate lookup (a
 * guest has no tenant of their own), set TenantContext before calling into
 * WorkOrderService (tenant-scoped, RLS), and best-effort notify the owner
 * afterward -- unlike RentalInquiryOrchestrator.send(), notification failure
 * here does NOT roll back the request, since the WorkOrder itself (visible
 * in XanuOS regardless) is the primary artifact, not the email.
 */
@Component
public class GuestMaintenanceRequestOrchestrator {

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final WorkOrderService workOrderService;
    private final NotificationService notificationService;

    public GuestMaintenanceRequestOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            WorkOrderService workOrderService, NotificationService notificationService) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.workOrderService = workOrderService;
        this.notificationService = notificationService;
    }

    public record MyMaintenanceRequestRow(UUID id, UUID propertyId, String propertyName, String category,
            String title, String description, String status, String resolutionNotes) {
    }

    public void send(UUID guestUserId, GuestMaintenanceRequestRequest request) {
        Map<String, Object> row = resolveProperty(request.propertyId());
        UUID tenantId = UUID.fromString(row.get("tenant_id").toString());
        UUID ownerUserId = UUID.fromString(row.get("owner_user_id").toString());
        String propertyName = row.get("name").toString();

        TenantContext.set(tenantId);
        WorkOrder workOrder;
        try {
            workOrder = workOrderService.create(guestUserId, new CreateWorkOrderRequest(
                    request.propertyId(), request.category(), request.title(), request.description(),
                    WorkOrderPriority.MEDIUM));
        } finally {
            TenantContext.clear();
        }
        notifyOwner(ownerUserId, propertyName, workOrder);
    }

    /**
     * Cross-tenant for the same reason RentalInquiryOrchestrator.myInquiries()
     * is -- the guest has no tenant of their own to scope an RLS-backed JPA
     * query by.
     */
    public List<MyMaintenanceRequestRow> myRequests(UUID guestUserId) {
        String sql = """
                SELECT w.id, w.property_id, p.name AS property_name, w.category, w.title, w.description,
                       w.status, w.resolution_notes
                FROM maintenance_work_orders w
                JOIN properties p ON p.id = w.property_id
                WHERE w.reported_by_user_id = ?
                ORDER BY w.created_at DESC
                """;
        return platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> new MyMaintenanceRequestRow(
                UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("property_id")),
                rs.getString("property_name"), rs.getString("category"), rs.getString("title"),
                rs.getString("description"), rs.getString("status"), rs.getString("resolution_notes")),
                guestUserId);
    }

    private void notifyOwner(UUID ownerUserId, String propertyName, WorkOrder workOrder) {
        try {
            String recipientAddress = resolveEmail(ownerUserId);
            if (recipientAddress == null) {
                return;
            }
            notificationService.send(new SendNotificationRequest(ownerUserId, recipientAddress,
                    "maintenance_request_owner",
                    Map.of("property_name", propertyName, "title", workOrder.getTitle())));
        } catch (Exception e) {
            // Best-effort, same reasoning as booking-confirmed emails elsewhere --
            // the WorkOrder is already created and visible to the owner in
            // XanuOS regardless of whether this notification succeeds.
        }
    }

    private Map<String, Object> resolveProperty(UUID propertyId) {
        List<Map<String, Object>> rows = platformAdminJdbcTemplate.queryForList(
                "SELECT tenant_id, owner_user_id, name FROM properties WHERE id = ?", propertyId);
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        return rows.get(0);
    }

    private String resolveEmail(UUID userId) {
        List<String> userEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM users WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        if (!userEmail.isEmpty()) {
            return userEmail.get(0);
        }
        List<String> guestEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM guest_profiles WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        return guestEmail.isEmpty() ? null : guestEmail.get(0);
    }
}
