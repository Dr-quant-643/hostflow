package com.hostflow.rental.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Deliberately NOT the same as module-identity's User — a rental tenant is often
 * NOT a system user at all (many landlords track tenants who never log into any
 * portal). linkedUserId is nullable, mirroring module-crm's Contact.linkedUserId
 * pattern, so a tenant CAN be linked to a real User account later (e.g. once the
 * rental portal, item 12, lets them self-service), without requiring it upfront.
 */
@Entity
@Table(name = "rental_tenants")
public class RentalTenant extends TenantScopedEntity {

    @Column(name = "linked_user_id")
    private java.util.UUID linkedUserId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    protected RentalTenant() {
    }

    public RentalTenant(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public void linkToUser(java.util.UUID userId) {
        this.linkedUserId = userId;
    }

    public java.util.UUID getLinkedUserId() {
        return linkedUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
