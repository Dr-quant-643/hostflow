package com.hostflow.crm.dto;

import com.hostflow.crm.entity.Interaction;

import java.time.Instant;
import java.util.UUID;

public record InteractionResponse(
        UUID id, UUID contactId, UUID loggedByUserId, String type, String notes, Instant occurredAt
) {
    public static InteractionResponse from(Interaction interaction) {
        return new InteractionResponse(
                interaction.getId(), interaction.getContactId(), interaction.getLoggedByUserId(),
                interaction.getType().name(), interaction.getNotes(), interaction.getCreatedAt());
    }
}
