package com.hostflow.rental.repository;

import com.hostflow.rental.entity.RentPayment;
import com.hostflow.rental.entity.RentPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RentPaymentRepository extends JpaRepository<RentPayment, UUID> {
    List<RentPayment> findByLeaseId(UUID leaseId);
    List<RentPayment> findByStatusAndDueDateBefore(RentPaymentStatus status, LocalDate date);
}
