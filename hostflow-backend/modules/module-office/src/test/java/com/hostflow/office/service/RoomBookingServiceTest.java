package com.hostflow.office.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.office.dto.CreateRoomBookingRequest;
import com.hostflow.office.entity.RoomBooking;
import com.hostflow.office.repository.RoomBookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomBookingServiceTest {

    @Mock
    private RoomBookingRepository repository;

    private RoomBookingService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new RoomBookingService(repository);
    }

    @Test
    void create_rejectsOverlappingBooking() {
        UUID roomId = UUID.randomUUID();
        Instant start = Instant.now();
        RoomBooking existing = new RoomBooking(roomId, UUID.randomUUID(), start, start.plus(1, ChronoUnit.HOURS), "Existing");
        when(repository.findOverlapping(any(), any(), any())).thenReturn(List.of(existing));

        CreateRoomBookingRequest request = new CreateRoomBookingRequest(roomId, start, start.plus(1, ChronoUnit.HOURS), "New");

        assertThatThrownBy(() -> service.create(UUID.randomUUID(), request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already booked");
    }
}
