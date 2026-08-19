package com.hostflow.maintenance.dto;

import com.hostflow.maintenance.entity.WorkOrder;

import java.util.UUID;

public record WorkOrderResponse(UUID id, UUID propertyId, String category, String title, String description,
                                 String priority, String status, UUID assignedTechnicianUserId, String resolutionNotes) {

    public static WorkOrderResponse from(WorkOrder wo) {
        return new WorkOrderResponse(wo.getId(), wo.getPropertyId(), wo.getCategory().name(), wo.getTitle(),
                wo.getDescription(), wo.getPriority().name(), wo.getStatus().name(),
                wo.getAssignedTechnicianUserId(), wo.getResolutionNotes());
    }
}
