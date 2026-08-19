package com.hostflow.office.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Uses Instant (not LocalDate) for start/end — meeting rooms are booked in hourly
 * slots, not whole days, unlike module-booking's Booking which is date-range
 * hospitality stays. Same overlap-check discipline as Booking.overlaps(), adapted
 * for timestamp ranges.
 */
@Entity
@Table(name = "office_room_bookings")
public class RoomBooking extends TenantScopedEntity {

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(name = "booked_by_user_id", nullable = false)
    private UUID bookedByUserId;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(name = "purpose")
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomBookingStatus status;

    protected RoomBooking() {
    }

    public RoomBooking(UUID roomId, UUID bookedByUserId, Instant startsAt, Instant endsAt, String purpose) {
        if (!endsAt.isAfter(startsAt)) {
            throw new BusinessRuleException("Meeting end time must be after start time");
        }
        this.roomId = roomId;
        this.bookedByUserId = bookedByUserId;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.purpose = purpose;
        this.status = RoomBookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == RoomBookingStatus.CANCELLED) {
            throw new BusinessRuleException("Booking is already cancelled");
        }
        this.status = RoomBookingStatus.CANCELLED;
    }

    public boolean overlaps(Instant otherStart, Instant otherEnd) {
        return this.startsAt.isBefore(otherEnd) && otherStart.isBefore(this.endsAt);
    }

    public UUID getRoomId() {
        return roomId;
    }

    public UUID getBookedByUserId() {
        return bookedByUserId;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public String getPurpose() {
        return purpose;
    }

    public RoomBookingStatus getStatus() {
        return status;
    }
}
