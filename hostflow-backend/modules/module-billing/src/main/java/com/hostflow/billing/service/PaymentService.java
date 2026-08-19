package com.hostflow.billing.service;

import com.hostflow.billing.entity.Payment;
import com.hostflow.billing.messaging.PaymentEventPublisher;
import com.hostflow.billing.repository.PaymentRepository;
import com.hostflow.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Fills a real gap: Payment entity/repository existed since module-billing's
 * original build, but no service/controller layer was ever built for it — flagged
 * explicitly in the audit. This is manual payment recording (e.g. staff recording
 * a cash/bank-transfer payment against an invoice); MPESA integration will later
 * call this same service's markSucceeded() upon a real payment callback, once
 * MPESA is built (deferred to the end, per your instruction).
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;

    public PaymentService(PaymentRepository paymentRepository, PaymentEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Payment recordAttempt(UUID invoiceId, BigDecimal amount, String providerReference) {
        return paymentRepository.save(new Payment(invoiceId, amount, providerReference));
    }

    @Transactional(readOnly = true)
    public List<Payment> listByInvoice(UUID invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    @Transactional
    public Payment markSucceeded(UUID paymentId) {
        Payment payment = getById(paymentId);
        payment.markSucceeded();
        eventPublisher.succeeded(payment);
        return payment;
    }

    @Transactional
    public Payment markFailed(UUID paymentId) {
        Payment payment = getById(paymentId);
        payment.markFailed();
        eventPublisher.failed(payment);
        return payment;
    }

    @Transactional
    public Payment refund(UUID paymentId) {
        Payment payment = getById(paymentId);
        payment.refund();
        eventPublisher.refunded(payment);
        return payment;
    }

    private Payment getById(UUID id) {
        return paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }
}
