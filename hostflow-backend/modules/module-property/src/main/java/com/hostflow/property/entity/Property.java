package com.hostflow.property.entity;

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

/**
 * The core physical-asset entity. Extends TenantScopedEntity (tenant_id + RLS,
 * following the pattern from module-identity's User entity exactly).
 *
 * latitude/longitude are stored as plain DOUBLE columns here, not as a native
 * PostGIS geometry type, deliberately: Hibernate's PostGIS mapping adds real
 * complexity (hibernate-spatial dependency, custom types) for marginal benefit
 * at
 * Phase 1 scale. Simple lat/lng columns support map-pin display and
 * straightforward
 * bounding-box queries; true PostGIS geometry (for radius/polygon search) can
 * be
 * added later as a migration without changing the Java model's public shape.
 *
 * embeddingVector is a UUID reference to a *future* pgvector-backed table
 * (property_embeddings), not an embedded column directly on this entity —
 * keeping
 * embedding storage/regeneration decoupled from the core property record, since
 * embeddings are recomputed independently (e.g. after description edits) and
 * doing
 * so shouldn't touch this entity's own version/updatedAt.
 */
@Entity
@Table(name = "properties")
public class Property extends TenantScopedEntity {

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_model", nullable = false)
    private RentalModel rentalModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PropertyStatus status;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "base_price", precision = 12, scale = 2)
    private BigDecimal basePrice;

    /**
     * Owner/manager-set "in use" override, independent of the Booking/Lease
     * system -- for cases those don't know about (e.g. a walk-in meeting in
     * an office hall). Purely informational on NazilCo: it does not block
     * date-level Booking availability, it just tells other guests when the
     * space frees up for logistics purposes. Null or in the past means
     * "not overridden" -- normal Booking/Lease-derived availability applies.
     */
    @Column(name = "manual_occupied_until")
    private Instant manualOccupiedUntil;

    /**
     * Owner-entered unit inventory (e.g. a 10-unit apartment building with 6
     * currently occupied) -- NOT derived from Booking/Lease data. The
     * booking system enforces exactly one active booking per property at
     * the database level (excl_bookings_no_overlap), so there is no live
     * per-unit signal to compute this from; the owner reports it directly,
     * same manual-input pattern as manualOccupiedUntil. Defaults keep every
     * existing single-unit property at 1/0 (0%) with no behavior change.
     */
    @Column(name = "total_units", nullable = false)
    private Integer totalUnits = 1;

    @Column(name = "occupied_units", nullable = false)
    private Integer occupiedUnits = 0;

    protected Property() {
    }

    public Property(UUID ownerUserId, String name, PropertyType propertyType, RentalModel rentalModel,
            String addressLine, String city, String country) {
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.propertyType = propertyType;
        this.rentalModel = rentalModel;
        this.status = PropertyStatus.DRAFT;
        this.addressLine = addressLine;
        this.city = city;
        this.country = country;
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * A property with no price published fine (no validation ever stopped
     * it), then guests hit a dead end on NazilCo -- the booking/inquiry card
     * has nothing to compute a total from. Blocking publish here is cheaper
     * than every downstream consumer having to guess why a listing is
     * unbookable.
     */
    public void publish() {
        if (basePrice == null) {
            String label = rentalModel == RentalModel.MONTHLY ? "a monthly rent" : "a nightly rate";
            throw new BusinessRuleException("Set " + label + " before publishing this property");
        }
        this.status = PropertyStatus.ACTIVE;
    }

    public void archive() {
        this.status = PropertyStatus.ARCHIVED;
    }

    /**
     * The only way back from ARCHIVED -- lands on DRAFT (not straight back to
     * ACTIVE) so the owner can review/update details before the existing
     * publish() flow re-validates and re-publishes it.
     */
    public void unarchive() {
        if (status != PropertyStatus.ARCHIVED) {
            throw new BusinessRuleException(
                    "Cannot unarchive a property with status " + status + " (expected ARCHIVED)");
        }
        this.status = PropertyStatus.DRAFT;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void markOccupiedUntil(Instant until) {
        this.manualOccupiedUntil = until;
    }

    public void clearOccupied() {
        this.manualOccupiedUntil = null;
    }

    public void updateUnitOccupancy(int totalUnits, int occupiedUnits) {
        if (totalUnits < 1) {
            throw new BusinessRuleException("Total units must be at least 1");
        }
        if (occupiedUnits < 0 || occupiedUnits > totalUnits) {
            throw new BusinessRuleException("Occupied units must be between 0 and total units");
        }
        this.totalUnits = totalUnits;
        this.occupiedUnits = occupiedUnits;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public void setAddress(String addressLine) {
        this.addressLine = addressLine;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public PropertyType getType() {
        return propertyType;
    }

    public RentalModel getRentalModel() {
        return rentalModel;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getAddress() {
        return addressLine;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public Instant getManualOccupiedUntil() {
        return manualOccupiedUntil;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public Integer getOccupiedUnits() {
        return occupiedUnits;
    }
}
