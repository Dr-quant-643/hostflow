package com.hostflow.maintenance.dto;

import com.hostflow.maintenance.entity.MaintenanceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMaintenanceScheduleRequest(
        @NotNull UUID propertyId, UUID assetId, @NotNull MaintenanceCategory category,
        @NotBlank String title, @NotNull @Positive Integer intervalDays, @NotNull LocalDate firstDueDate) {
}
