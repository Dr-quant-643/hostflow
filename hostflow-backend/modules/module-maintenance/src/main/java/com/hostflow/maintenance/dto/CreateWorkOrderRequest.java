package com.hostflow.maintenance.dto;

import com.hostflow.maintenance.entity.MaintenanceCategory;
import com.hostflow.maintenance.entity.WorkOrderPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWorkOrderRequest(
        @NotNull UUID propertyId,
        @NotNull MaintenanceCategory category,
        @NotBlank String title,
        String description,
        @NotNull WorkOrderPriority priority
) {
}
