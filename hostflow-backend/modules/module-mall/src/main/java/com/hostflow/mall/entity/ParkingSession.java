package com.hostflow.mall.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mall_parking_sessions")
public class ParkingSession extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "vehicle_plate", nullable = false)
    private String vehiclePlate;

    @Column(name = "entered_at", nullable = false)
    private Instant enteredAt;

    @Column(name = "exited_at")
    private Instant exitedAt;

    @Column(name = "fee_charged", precision = 10, scale = 2)
    private BigDecimal feeCharged;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParkingSessionStatus status;

    protected ParkingSession() {
    }

    public ParkingSession(UUID propertyId, String vehiclePlate) {
        this.propertyId = propertyId;
        this.vehiclePlate = vehiclePlate;
        this.enteredAt = Instant.now();
        this.status = ParkingSessionStatus.ACTIVE;
    }

    /**
     * Simple flat-rate-per-hour fee calc, rounded up to the nearest hour — a
     * reasonable Phase 1 default; a real mall would likely want configurable
     * tiered rates, flagged as a future enhancement rather than over-built now.
     */
    public void exit(BigDecimal hourlyRate) {
        if (status != ParkingSessionStatus.ACTIVE) {
            throw new BusinessRuleException("Parking session already completed");
        }
        this.exitedAt = Instant.now();
        long minutesParked = java.time.Duration.between(enteredAt, exitedAt).toMinutes();
        long hoursRoundedUp = (minutesParked + 59) / 60;
        this.feeCharged = hourlyRate.multiply(BigDecimal.valueOf(Math.max(hoursRoundedUp, 1)));
        this.status = ParkingSessionStatus.COMPLETED;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public Instant getEnteredAt() {
        return enteredAt;
    }

    public Instant getExitedAt() {
        return exitedAt;
    }

    public BigDecimal getFeeCharged() {
        return feeCharged;
    }

    public ParkingSessionStatus getStatus() {
        return status;
    }
}
