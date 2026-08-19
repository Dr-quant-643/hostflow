package com.hostflow.maintenance.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "maintenance_work_orders")
public class WorkOrder extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "reported_by_user_id")
    private UUID reportedByUserId;

    @Column(name = "assigned_technician_user_id")
    private UUID assignedTechnicianUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MaintenanceCategory category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private WorkOrderPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkOrderStatus status;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    protected WorkOrder() {
    }

    public WorkOrder(UUID propertyId, UUID reportedByUserId, MaintenanceCategory category,
                      String title, String description, WorkOrderPriority priority) {
        this.propertyId = propertyId;
        this.reportedByUserId = reportedByUserId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = WorkOrderStatus.OPEN;
    }

    public void assign(UUID technicianUserId) {
        if (status == WorkOrderStatus.COMPLETED || status == WorkOrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot assign a work order with status " + status);
        }
        this.assignedTechnicianUserId = technicianUserId;
        this.status = WorkOrderStatus.ASSIGNED;
    }

    public void startWork() {
        if (status != WorkOrderStatus.ASSIGNED) {
            throw new BusinessRuleException("Cannot start work on a work order with status " + status + " (expected ASSIGNED)");
        }
        this.status = WorkOrderStatus.IN_PROGRESS;
    }

    public void complete(String resolutionNotes) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Cannot complete a work order with status " + status + " (expected IN_PROGRESS)");
        }
        this.status = WorkOrderStatus.COMPLETED;
        this.resolutionNotes = resolutionNotes;
    }

    public void cancel() {
        if (status == WorkOrderStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot cancel a COMPLETED work order");
        }
        this.status = WorkOrderStatus.CANCELLED;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getReportedByUserId() {
        return reportedByUserId;
    }

    public UUID getAssignedTechnicianUserId() {
        return assignedTechnicianUserId;
    }

    public MaintenanceCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public WorkOrderPriority getPriority() {
        return priority;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }
}
