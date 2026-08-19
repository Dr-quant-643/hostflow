package com.hostflow.crm.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupportTicketEntityTest {

    private SupportTicket newTicket() {
        return new SupportTicket(UUID.randomUUID(), UUID.randomUUID(), "Can't log in",
                "Password reset link not arriving", TicketPriority.HIGH, TicketProductScope.NAZILCO);
    }

    @Test
    void newTicket_startsOpen() {
        assertThat(newTicket().getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    void assign_fromOpen_movesToInProgress() {
        SupportTicket ticket = newTicket();
        UUID staffId = UUID.randomUUID();

        ticket.assign(staffId);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getAssignedToUserId()).isEqualTo(staffId);
    }

    @Test
    void assign_onClosedTicket_throws() {
        SupportTicket ticket = newTicket();
        ticket.assign(UUID.randomUUID());
        ticket.resolve();
        ticket.close();

        assertThatThrownBy(() -> ticket.assign(UUID.randomUUID()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("CLOSED");
    }

    @Test
    void fullLifecycle_openToInProgressToResolvedToClosed() {
        SupportTicket ticket = newTicket();

        ticket.assign(UUID.randomUUID());
        ticket.resolve();
        ticket.close();

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CLOSED);
    }

    @Test
    void reopen_fromResolved_movesBackToInProgress() {
        SupportTicket ticket = newTicket();
        ticket.assign(UUID.randomUUID());
        ticket.resolve();

        ticket.reopen();

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void close_beforeResolved_throws() {
        SupportTicket ticket = newTicket();

        assertThatThrownBy(ticket::close)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("expected RESOLVED");
    }
}
