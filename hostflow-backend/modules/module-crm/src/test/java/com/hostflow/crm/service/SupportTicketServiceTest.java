package com.hostflow.crm.service;

import com.hostflow.crm.dto.CreateSupportTicketRequest;
import com.hostflow.crm.entity.*;
import com.hostflow.crm.repository.InteractionRepository;
import com.hostflow.crm.repository.SupportTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;
    @Mock
    private InteractionRepository interactionRepository;

    private SupportTicketService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new SupportTicketService(ticketRepository, interactionRepository);
    }

    @Test
    void create_withLinkedContact_logsInteraction() {
        UUID contactId = UUID.randomUUID();
        UUID raisedById = UUID.randomUUID();
        CreateSupportTicketRequest request = new CreateSupportTicketRequest(
                contactId, "Payment issue", "Card declined", TicketPriority.MEDIUM, TicketProductScope.XANUOS);
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(raisedById, request);

        verify(interactionRepository).save(argThat(interaction -> interaction.getContactId().equals(contactId)
                && interaction.getType() == InteractionType.SUPPORT_REQUEST));
    }

    @Test
    void create_withNoLinkedContact_doesNotLogInteraction() {
        CreateSupportTicketRequest request = new CreateSupportTicketRequest(
                null, "Internal staff issue", null, TicketPriority.LOW, TicketProductScope.XANUOS);
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(UUID.randomUUID(), request);

        verify(interactionRepository, never()).save(any());
    }

    @Test
    void assign_callsEntityAssign_andLogsInteractionWhenContactLinked() {
        UUID contactId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        SupportTicket ticket = new SupportTicket(contactId, UUID.randomUUID(), "Issue",
                "desc", TicketPriority.HIGH, TicketProductScope.NAZILCO);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        service.assign(ticketId, staffId);

        assert ticket.getStatus() == TicketStatus.IN_PROGRESS;
        verify(interactionRepository).save(any());
    }
}
