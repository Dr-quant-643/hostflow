package com.hostflow.crm.repository;

import com.hostflow.crm.entity.SupportTicket;
import com.hostflow.crm.entity.TicketProductScope;
import com.hostflow.crm.entity.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    Page<SupportTicket> findByProductScopeAndStatus(TicketProductScope productScope, TicketStatus status,
            Pageable pageable);

    Page<SupportTicket> findByProductScope(TicketProductScope productScope, Pageable pageable);

    Page<SupportTicket> findByAssignedToUserId(UUID assignedToUserId, Pageable pageable);
}
