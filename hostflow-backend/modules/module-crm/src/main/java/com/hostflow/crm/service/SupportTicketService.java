package com.hostflow.crm.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.crm.dto.CreateSupportTicketRequest;
import com.hostflow.crm.entity.Interaction;
import com.hostflow.crm.entity.InteractionType;
import com.hostflow.crm.entity.SupportTicket;
import com.hostflow.crm.entity.TicketProductScope;
import com.hostflow.crm.entity.TicketStatus;
import com.hostflow.crm.repository.InteractionRepository;
import com.hostflow.crm.repository.SupportTicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Every status/assignment change writes a SYSTEM_EVENT Interaction against the
 * linked contact, IF one exists (contactId is nullable — staff-raised tickets
 * with
 * no CRM contact simply skip this, rather than failing). Keeps the append-only
 * interaction history and the ticket's own status as two separate, consistent
 * views of the same event, per the design decided during reconciliation.
 */
@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final InteractionRepository interactionRepository;

    public SupportTicketService(SupportTicketRepository ticketRepository, InteractionRepository interactionRepository) {
        this.ticketRepository = ticketRepository;
        this.interactionRepository = interactionRepository;
    }

    @Transactional
    public SupportTicket create(UUID raisedByUserId, CreateSupportTicketRequest request) {
        SupportTicket ticket = new SupportTicket(
                request.contactId(), raisedByUserId, request.subject(), request.description(),
                request.priority(), request.productScope());
        ticket = ticketRepository.save(ticket);
        logInteractionIfContactLinked(ticket, raisedByUserId, "Support ticket raised: " + request.subject());
        return ticket;
    }

    @Transactional(readOnly = true)
    public SupportTicket getById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", ticketId));
    }

    @Transactional(readOnly = true)
    public Page<SupportTicket> list(TicketProductScope productScope, TicketStatus status, int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset / Math.max(limit, 1), limit);
        return status != null
                ? ticketRepository.findByProductScopeAndStatus(productScope, status, pageRequest)
                : ticketRepository.findByProductScope(productScope, pageRequest);
    }

    @Transactional
    public SupportTicket assign(UUID ticketId, UUID staffUserId) {
        SupportTicket ticket = getById(ticketId);
        ticket.assign(staffUserId);
        logInteractionIfContactLinked(ticket, staffUserId, "Ticket assigned to staff member " + staffUserId);
        return ticket;
    }

    @Transactional
    public SupportTicket resolve(UUID ticketId, UUID actingUserId) {
        SupportTicket ticket = getById(ticketId);
        ticket.resolve();
        logInteractionIfContactLinked(ticket, actingUserId, "Ticket marked RESOLVED");
        return ticket;
    }

    @Transactional
    public SupportTicket reopen(UUID ticketId, UUID actingUserId) {
        SupportTicket ticket = getById(ticketId);
        ticket.reopen();
        logInteractionIfContactLinked(ticket, actingUserId, "Ticket reopened");
        return ticket;
    }

    @Transactional
    public SupportTicket close(UUID ticketId, UUID actingUserId) {
        SupportTicket ticket = getById(ticketId);
        ticket.close();
        logInteractionIfContactLinked(ticket, actingUserId, "Ticket CLOSED");
        return ticket;
    }

    private void logInteractionIfContactLinked(SupportTicket ticket, UUID actingUserId, String note) {
        if (ticket.getContactId() != null) {
            interactionRepository.save(
                    new Interaction(ticket.getContactId(), actingUserId, InteractionType.SUPPORT_REQUEST, note));
        }
    }
}
