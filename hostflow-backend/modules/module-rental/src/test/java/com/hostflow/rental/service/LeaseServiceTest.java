package com.hostflow.rental.service;

import com.hostflow.rental.dto.CreateLeaseRequest;
import com.hostflow.rental.entity.Lease;
import com.hostflow.rental.repository.LeaseRepository;
import com.hostflow.rental.repository.RentPaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaseServiceTest {

    @Mock
    private LeaseRepository leaseRepository;
    @Mock
    private RentPaymentRepository rentPaymentRepository;

    private LeaseService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new LeaseService(leaseRepository, rentPaymentRepository);
    }

    @Test
    void activate_generatesOneRentPaymentPerMonth() {
        UUID leaseId = UUID.randomUUID();
        // 12-month lease: Jan 1 - Dec 1 (11 full months between, per ChronoUnit.MONTHS
        // semantics used in generateRentSchedule — documented behavior, not a bug:
        // a lease from month-start to month-start N months later generates N payments).
        Lease lease = new Lease(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), BigDecimal.valueOf(1000), null);
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(lease));

        service.activate(leaseId);

        ArgumentCaptor<com.hostflow.rental.entity.RentPayment> captor =
                ArgumentCaptor.forClass(com.hostflow.rental.entity.RentPayment.class);
        verify(rentPaymentRepository, org.mockito.Mockito.times(12)).save(captor.capture());
        assertThat(lease.getStatus().name()).isEqualTo("ACTIVE");
    }
}
