package com.hostflow.app.publicapi;

import com.hostflow.booking.dto.CreateBookingRequest;
import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.messaging.BookingEventPublisher;
import com.hostflow.booking.repository.BookingRepository;
import com.hostflow.booking.service.BookingAvailabilityService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class GuestBookingWriter {

    private final BookingRepository bookingRepository;
    private final BookingAvailabilityService availabilityService;
    private final BookingEventPublisher eventPublisher;

    public GuestBookingWriter(BookingRepository bookingRepository, BookingAvailabilityService availabilityService,
                               BookingEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.availabilityService = availabilityService;
        this.eventPublisher = eventPublisher;
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

    @Transactional
    public Booking confirm(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.confirm();
        eventPublisher.confirmed(booking, booking.getGuestUserId());
        return booking;
    }

    @Transactional
    public Booking cancel(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.cancel();
        eventPublisher.cancelled(booking, booking.getGuestUserId());
        return booking;
    }
}
