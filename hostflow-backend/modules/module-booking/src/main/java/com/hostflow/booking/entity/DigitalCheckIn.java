package com.hostflow.booking.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking_digital_checkins")
public class DigitalCheckIn extends TenantScopedEntity {

    @Column(name = "booking_id", nullable = false, unique = true)
    private UUID bookingId;

    @Column(name = "id_document_object_key")
    private String idDocumentObjectKey;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    protected DigitalCheckIn() {
    }

    public DigitalCheckIn(UUID bookingId, String idDocumentObjectKey) {
        this.bookingId = bookingId;
        this.idDocumentObjectKey = idDocumentObjectKey;
        this.confirmedAt = Instant.now();
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public String getIdDocumentObjectKey() {
        return idDocumentObjectKey;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }
}