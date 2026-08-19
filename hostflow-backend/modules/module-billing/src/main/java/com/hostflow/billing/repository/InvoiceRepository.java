package com.hostflow.billing.repository;

import com.hostflow.billing.entity.Invoice;
import com.hostflow.billing.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);
}
