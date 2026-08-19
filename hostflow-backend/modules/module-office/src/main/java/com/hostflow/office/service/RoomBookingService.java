package com.hostflow.office.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.office.dto.CreateRoomBookingRequest;
import com.hostflow.office.entity.RoomBooking;
import com.hostflow.office.repository.RoomBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RoomBookingService {

    private final RoomBookingRepository repository;

    public RoomBookingService(RoomBookingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RoomBooking create(UUID bookedByUserId, CreateRoomBookingRequest request) {
        if (!repository.findOverlapping(request.roomId(), request.startsAt(), request.endsAt()).isEmpty()) {
            throw new BusinessRuleException("Meeting room is already booked for this time slot");
        }
        RoomBooking booking = new RoomBooking(request.roomId(), bookedByUserId, request.startsAt(), request.endsAt(), request.purpose());
        return repository.save(booking);
    }

    @Transactional
    public RoomBooking cancel(UUID id) {
        RoomBooking booking = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RoomBooking", id));
        booking.cancel();
        return booking;
    }
}
