package com.hostflow.rental.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.rental.entity.RentPayment;
import com.hostflow.rental.repository.RentPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RentPaymentService {

    private final RentPaymentRepository repository;

    public RentPaymentService(RentPaymentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RentPayment> listByLease(UUID leaseId) {
        return repository.findByLeaseId(leaseId);
    }

    @Transactional
    public RentPayment markPaid(UUID id) {
        RentPayment payment = getById(id);
        payment.markPaid(LocalDate.now());
        return payment;
    }

    @Transactional
    public RentPayment waive(UUID id) {
        RentPayment payment = getById(id);
        payment.waive();
        return payment;
    }

    private RentPayment getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RentPayment", id));
    }
}
