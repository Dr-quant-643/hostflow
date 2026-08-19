package com.hostflow.mall.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.mall.dto.ParkingEntryRequest;
import com.hostflow.mall.entity.ParkingSession;
import com.hostflow.mall.entity.ParkingSessionStatus;
import com.hostflow.mall.repository.ParkingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingSessionRepository repository;

    private ParkingService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new ParkingService(repository);
    }

    @Test
    void enter_rejectsVehicleAlreadyParked() {
        UUID propertyId = UUID.randomUUID();
        ParkingSession existing = new ParkingSession(propertyId, "KAA123B");
        when(repository.findByPropertyIdAndVehiclePlateAndStatus(propertyId, "KAA123B", ParkingSessionStatus.ACTIVE))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.enter(new ParkingEntryRequest(propertyId, "KAA123B")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already has an active");
    }
}
