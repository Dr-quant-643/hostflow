package com.hostflow.crm.dto;

import com.hostflow.crm.entity.TicketPriority;
import com.hostflow.crm.entity.TicketProductScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSupportTicketRequest(
        UUID contactId,
        @NotBlank String subject,
        String description,
        @NotNull TicketPriority priority,
        @NotNull TicketProductScope productScope) {
}
