package com.hostflow.booking.service;

import com.hostflow.booking.dto.CreateBookingRequest;
import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.messaging.BookingEventPublisher;
import com.hostflow.booking.repository.BookingRepository;
import com.hostflow.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingAvailabilityService availabilityService;
    private final BookingEventPublisher eventPublisher;

    public BookingService(BookingRepository bookingRepository, BookingAvailabilityService availabilityService,
                           BookingEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.availabilityService = availabilityService;
        this.eventPublisher = eventPublisher;
    }

    /** Staff-facing oversight list, RLS-scoped to the caller's own tenant. */
    @Transactional(readOnly = true)
    public List<Booking> list(int limit, int offset) {
        return bookingRepository.findAll(PageRequest.of(offset / Math.max(limit, 1), limit)).getContent();
    }

    @Transactional
    public Booking create(UUID guestUserId, CreateBookingRequest request) {
        availabilityService.assertAvailable(request.propertyId(), request.checkIn(), request.checkOut());

        Booking booking = new Booking(
                request.propertyId(), guestUserId, request.checkIn(), request.checkOut(), request.totalPrice());
        booking = bookingRepository.save(booking);
        eventPublisher.created(booking, guestUserId);
        return booking;
    }

    @Transactional(readOnly = true)
    public Booking getById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
    }

    @Transactional
    public Booking confirm(UUID bookingId, UUID actorUserId) {
        Booking booking = getById(bookingId);
        booking.confirm();
        eventPublisher.confirmed(booking, actorUserId);
        return booking;
    }

    @Transactional
    public Booking cancel(UUID bookingId, UUID actorUserId) {
        Booking booking = getById(bookingId);
        booking.cancel();
        eventPublisher.cancelled(booking, actorUserId);
        return booking;
    }
}
