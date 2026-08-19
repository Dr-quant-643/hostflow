package com.hostflow.crm.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTicketRequest(@NotNull UUID staffUserId) {
}
