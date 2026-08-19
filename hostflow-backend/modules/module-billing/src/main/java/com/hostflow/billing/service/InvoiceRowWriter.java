package com.hostflow.billing.service;

import com.hostflow.billing.dto.CreateInvoiceRequest;
import com.hostflow.billing.entity.Invoice;
import com.hostflow.billing.repository.InvoiceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * THE FIX for the REQUIRES_NEW self-invocation bug flagged in module-billing's own
 * report (open item #24). Extracted into its OWN bean so that when InvoiceService
 * calls writeRow() via constructor-injected dependency (not self-invocation), the
 * call passes through Spring's real transactional proxy and REQUIRES_NEW is
 * correctly honored — each row commits/rolls back independently as originally
 * intended.
 */
@Component
public class InvoiceRowWriter {

    private final InvoiceRepository invoiceRepository;

    public InvoiceRowWriter(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Invoice writeRow(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice(request.bookingId(), request.billedUserId(), request.amount(), request.dueDate());
        return invoiceRepository.save(invoice);
    }
}
