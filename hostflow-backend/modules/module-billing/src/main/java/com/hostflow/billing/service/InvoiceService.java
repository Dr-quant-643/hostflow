package com.hostflow.billing.service;

import com.hostflow.billing.dto.BatchInvoiceResult;
import com.hostflow.billing.dto.CreateInvoiceRequest;
import com.hostflow.billing.entity.Invoice;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.billing.repository.InvoiceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceRowWriter invoiceRowWriter;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceRowWriter invoiceRowWriter) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceRowWriter = invoiceRowWriter;
    }

    @Transactional(readOnly = true)
    public List<Invoice> list(int limit, int offset) {
        return invoiceRepository.findAll(PageRequest.of(offset / Math.max(limit, 1), limit)).getContent();
    }

    @Transactional
    public Invoice create(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice(request.bookingId(), request.billedUserId(), request.amount(), request.dueDate());
        return invoiceRepository.save(invoice);
    }

    /**
     * FIXED (previously self-invoked createSingleInTransaction, which silently
     * ignored REQUIRES_NEW because Spring's proxy-based AOP cannot intercept
     * self-invocation). Now delegates to InvoiceRowWriter, a genuinely separate
     * Spring bean, so each row's REQUIRES_NEW transaction is correctly honored via
     * the real proxy — one row's failure no longer risks affecting others, and
     * successful rows commit independently even if a later row in the same batch
     * fails.
     */
    public List<BatchInvoiceResult> createBatch(List<CreateInvoiceRequest> requests) {
        List<BatchInvoiceResult> results = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            try {
                Invoice invoice = invoiceRowWriter.writeRow(requests.get(i));
                results.add(BatchInvoiceResult.success(i, invoice.getId()));
            } catch (Exception e) {
                results.add(BatchInvoiceResult.failure(i, e.getMessage()));
            }
        }
        return results;
    }

    @Transactional(readOnly = true)
    public Invoice getById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
    }

    @Transactional
    public Invoice issue(UUID invoiceId) {
        Invoice invoice = getById(invoiceId);
        invoice.issue();
        return invoice;
    }

    @Transactional
    public Invoice markPaid(UUID invoiceId) {
        Invoice invoice = getById(invoiceId);
        invoice.markPaid();
        return invoice;
    }
}
