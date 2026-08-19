package com.hostflow.crm.dto;

import com.hostflow.crm.entity.InteractionType;
import jakarta.validation.constraints.NotNull;

public record LogInteractionRequest(
        @NotNull InteractionType type,
        String notes
) {
}
