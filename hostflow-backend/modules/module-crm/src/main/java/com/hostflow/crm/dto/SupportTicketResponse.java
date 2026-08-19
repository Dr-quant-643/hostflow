package com.hostflow.crm.dto;

import com.hostflow.crm.entity.SupportTicket;

import java.util.UUID;

public record SupportTicketResponse(UUID id, UUID contactId, String subject, String description,
        String priority, String status, String productScope, UUID assignedToUserId) {

    public static SupportTicketResponse from(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(), ticket.getContactId(), ticket.getSubject(), ticket.getDescription(),
                ticket.getPriority().name(), ticket.getStatus().name(), ticket.getProductScope().name(),
                ticket.getAssignedToUserId());
    }
}
