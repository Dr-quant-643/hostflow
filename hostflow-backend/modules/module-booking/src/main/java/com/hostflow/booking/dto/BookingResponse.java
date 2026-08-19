package com.hostflow.booking.dto;

import com.hostflow.booking.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookingResponse(
        UUID id, UUID propertyId, UUID guestUserId,
        LocalDate checkIn, LocalDate checkOut, String status, BigDecimal totalPrice
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(), booking.getPropertyId(), booking.getGuestUserId(),
                booking.getCheckIn(), booking.getCheckOut(), booking.getStatus().name(), booking.getTotalPrice()
        );
    }
}
