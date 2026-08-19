package com.hostflow.maintenance.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTechnicianRequest(@NotNull UUID technicianUserId) {
}
