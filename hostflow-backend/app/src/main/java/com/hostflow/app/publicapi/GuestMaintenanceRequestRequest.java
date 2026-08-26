package com.hostflow.app.publicapi;

import com.hostflow.maintenance.entity.MaintenanceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GuestMaintenanceRequestRequest(
        @NotNull UUID propertyId,
        @NotNull MaintenanceCategory category,
        @NotBlank String title,
        String description
) {
}
