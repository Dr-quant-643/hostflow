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

@Entity
@Table(name = "office_visitors")
public class Visitor extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "hosted_by_user_id", nullable = false)
    private UUID hostedByUserId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "company")
    private String company;

    @Column(name = "expected_at", nullable = false)
    private Instant expectedAt;

    @Column(name = "checked_in_at")
    private Instant checkedInAt;

    @Column(name = "checked_out_at")
    private Instant checkedOutAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VisitorStatus status;

    protected Visitor() {
    }

    public Visitor(UUID propertyId, UUID hostedByUserId, String fullName, String company, Instant expectedAt) {
        this.propertyId = propertyId;
        this.hostedByUserId = hostedByUserId;
        this.fullName = fullName;
        this.company = company;
        this.expectedAt = expectedAt;
        this.status = VisitorStatus.EXPECTED;
    }

    public void checkIn() {
        if (status != VisitorStatus.EXPECTED) {
            throw new BusinessRuleException("Cannot check in a visitor with status " + status + " (expected EXPECTED)");
        }
        this.status = VisitorStatus.CHECKED_IN;
        this.checkedInAt = Instant.now();
    }

    public void checkOut() {
        if (status != VisitorStatus.CHECKED_IN) {
            throw new BusinessRuleException("Cannot check out a visitor with status " + status + " (expected CHECKED_IN)");
        }
        this.status = VisitorStatus.CHECKED_OUT;
        this.checkedOutAt = Instant.now();
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getHostedByUserId() {
        return hostedByUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCompany() {
        return company;
    }

    public Instant getExpectedAt() {
        return expectedAt;
    }

    public Instant getCheckedInAt() {
        return checkedInAt;
    }

    public Instant getCheckedOutAt() {
        return checkedOutAt;
    }

    public VisitorStatus getStatus() {
        return status;
    }
}
