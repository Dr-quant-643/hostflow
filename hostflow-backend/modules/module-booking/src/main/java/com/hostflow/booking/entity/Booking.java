package com.hostflow.booking.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * References Property by UUID only (propertyId column) — no JPA @ManyToOne/foreign
 * key relationship to module-property's Property entity, since these are independent
 * modules with no Java-level dependency between them, per the architecture decision.
 * Referential integrity across modules is enforced at the DB level via a plain FK
 * constraint in the Flyway migration (V7), not via JPA relationship mapping.
 */
@Entity
@Table(name = "bookings")
public class Booking extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "guest_user_id", nullable = false)
    private UUID guestUserId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String declineReason;

    protected Booking() {
    }

    public Booking(UUID propertyId, UUID guestUserId, LocalDate checkIn, LocalDate checkOut, BigDecimal totalPrice) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessRuleException("Check-out date must be after check-in date");
        }
        this.propertyId = propertyId;
        this.guestUserId = guestUserId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = BookingStatus.PENDING;
        this.totalPrice = totalPrice;
    }

    public void confirm() {
        requireStatus(BookingStatus.PENDING, "confirm");
        this.status = BookingStatus.CONFIRMED;
    }

    public void decline(String reason) {
        requireStatus(BookingStatus.PENDING, "decline");
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleException("A reason is required to decline a booking");
        }
        this.status = BookingStatus.DECLINED;
        this.declineReason = reason;
    }

    public void checkIn() {
        requireStatus(BookingStatus.CONFIRMED, "check in");
        this.status = BookingStatus.CHECKED_IN;
    }

    public void checkOut() {
        requireStatus(BookingStatus.CHECKED_IN, "check out");
        this.status = BookingStatus.CHECKED_OUT;
    }

    public void cancel() {
        if (status == BookingStatus.CHECKED_OUT || status == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot cancel a booking that is already " + status);
        }
        this.status = BookingStatus.CANCELLED;
    }

    private void requireStatus(BookingStatus required, String action) {
        if (status != required) {
            throw new BusinessRuleException(
                    "Cannot " + action + " a booking with status " + status + " (expected " + required + ")");
        }
    }

    /**
     * True if this booking's date range overlaps the given range. Standard interval
     * overlap check: two ranges [aStart,aEnd) and [bStart,bEnd) overlap iff
     * aStart < bEnd AND bStart < aEnd. Check-out day itself is NOT considered occupied
     * (a guest checking out on day X and another checking in on day X is allowed),
     * hence the strict '<' rather than '<=' — half-open interval semantics.
     */
    public boolean overlaps(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return this.checkIn.isBefore(otherCheckOut) && otherCheckIn.isBefore(this.checkOut);
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getGuestUserId() {
        return guestUserId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getDeclineReason() {
        return declineReason;
    }
}
