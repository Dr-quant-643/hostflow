package com.hostflow.mall.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Deliberately a SEPARATE entity from module-rental's RentalTenant, even though
 * both represent "someone who occupies a unit and pays rent" — retail leasing has
 * different fields (business name, revenue-share percentage for percentage-rent
 * arrangements common in malls) that don't belong on a residential lease. Kept
 * distinct rather than forcing one shared abstraction across meaningfully
 * different business domains.
 */
@Entity
@Table(name = "mall_retail_tenants")
public class RetailTenant extends TenantScopedEntity {

    @Column(name = "retail_unit_id", nullable = false)
    private UUID retailUnitId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "monthly_rent", precision = 12, scale = 2, nullable = false)
    private BigDecimal monthlyRent;

    @Column(name = "revenue_share_percent", precision = 5, scale = 2)
    private BigDecimal revenueSharePercent;

    protected RetailTenant() {
    }

    public RetailTenant(UUID retailUnitId, String businessName, String contactEmail, String contactPhone,
                         BigDecimal monthlyRent, BigDecimal revenueSharePercent) {
        this.retailUnitId = retailUnitId;
        this.businessName = businessName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.monthlyRent = monthlyRent;
        this.revenueSharePercent = revenueSharePercent;
    }

    public UUID getRetailUnitId() {
        return retailUnitId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public BigDecimal getRevenueSharePercent() {
        return revenueSharePercent;
    }
}
