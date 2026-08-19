package com.hostflow.maintenance.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Named explicitly in the vision doc's Maintenance Management section
 * ("Asset management") but never built. Tracks physical equipment/assets per
 * property — HVAC units, appliances, etc. — with warranty/purchase tracking, so
 * a work order can eventually reference "which specific asset broke," not just a
 * free-text description.
 */
@Entity
@Table(name = "maintenance_assets")
public class Asset extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected Asset() {
    }

    public Asset(UUID propertyId, String name, String category, String serialNumber,
                 LocalDate purchaseDate, LocalDate warrantyExpiryDate) {
        this.propertyId = propertyId;
        this.name = name;
        this.category = category;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public void decommission() {
        this.active = false;
    }

    public boolean isUnderWarranty() {
        return warrantyExpiryDate != null && !warrantyExpiryDate.isBefore(LocalDate.now());
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public boolean isActive() {
        return active;
    }
}
