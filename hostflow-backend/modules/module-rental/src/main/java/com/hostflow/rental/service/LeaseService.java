package com.hostflow.rental.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.rental.dto.CreateLeaseRequest;
import com.hostflow.rental.entity.Lease;
import com.hostflow.rental.entity.LeaseStatus;
import com.hostflow.rental.entity.RentPayment;
import com.hostflow.rental.repository.LeaseRepository;
import com.hostflow.rental.repository.RentPaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final RentPaymentRepository rentPaymentRepository;

    public LeaseService(LeaseRepository leaseRepository, RentPaymentRepository rentPaymentRepository) {
        this.leaseRepository = leaseRepository;
        this.rentPaymentRepository = rentPaymentRepository;
    }

    @Transactional
    public Lease decline(UUID id, String reason) {
        Lease lease = getById(id);
        lease.decline(reason);
        return leaseRepository.save(lease);
    }

    @Transactional(readOnly = true)
    public long countDraft() {
        return leaseRepository.countByStatus(LeaseStatus.DRAFT);
    }

    @Transactional(readOnly = true)
    public long countByStatus(LeaseStatus status) {
        return leaseRepository.countByStatus(status);
    }

    @Transactional
    public Lease create(CreateLeaseRequest request) {
        Lease lease = new Lease(request.propertyId(), request.tenantIdRef(), request.startDate(),
                request.endDate(), request.monthlyRent(), request.securityDeposit());
        return leaseRepository.save(lease);
    }

    @Transactional(readOnly = true)
    public Lease getById(UUID id) {
        return leaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lease", id));
    }

    @Transactional(readOnly = true)
    public Page<Lease> listByProperty(UUID propertyId, int limit, int offset) {
        return leaseRepository.findByPropertyId(propertyId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    /**
     * Activation generates the FULL rent payment schedule up front (one RentPayment
     * row per month of the lease term, due on the same day-of-month as the lease
     * start date) — this is a deliberate design choice: the payment schedule is
     * visible and trackable from day one, rather than being created reactively
     * only as each month arrives, which would make "upcoming rent due" reporting
     * impossible without a scheduled job constantly generating new rows.
     */
    @Transactional
    public Lease activate(UUID id) {
        Lease lease = getById(id);
        lease.activate();
        generateRentSchedule(lease);
        return lease;
    }

    private void generateRentSchedule(Lease lease) {
        long months = ChronoUnit.MONTHS.between(
                lease.getStartDate().withDayOfMonth(1), lease.getEndDate().withDayOfMonth(1));
        for (int i = 0; i < months; i++) {
            LocalDate dueDate = lease.getStartDate().plusMonths(i);
            rentPaymentRepository.save(new RentPayment(lease.getId(), dueDate, lease.getMonthlyRent()));
        }
    }

    @Transactional
    public Lease terminate(UUID id) {
        Lease lease = getById(id);
        lease.terminate();
        return lease;
    }
}
