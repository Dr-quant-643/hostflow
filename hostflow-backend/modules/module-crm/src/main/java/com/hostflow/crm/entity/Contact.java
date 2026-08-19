package com.hostflow.crm.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Deliberately does NOT reference module-identity's User entity or
 * module-booking's Booking entity via any Java relationship — a Contact may exist
 * for a prospect who has never created a User account (e.g. a lead captured from a
 * marketing form). Where a Contact later becomes a real User, linking is done via
 * a plain nullable UUID column (linkedUserId), consistent with how module-booking
 * references module-property by UUID only.
 */
@Entity
@Table(name = "crm_contacts")
public class Contact extends TenantScopedEntity {

    @Column(name = "linked_user_id")
    private java.util.UUID linkedUserId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "source")
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContactStatus status;

    protected Contact() {
    }

    public Contact(String fullName, String email, String phone, String source) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.source = source;
        this.status = ContactStatus.LEAD;
    }

    public void qualify() {
        requireStatus(ContactStatus.LEAD, "qualify");
        this.status = ContactStatus.QUALIFIED;
    }

    public void convertToCustomer(java.util.UUID linkedUserId) {
        if (status == ContactStatus.CUSTOMER || status == ContactStatus.LOST) {
            throw new BusinessRuleException("Cannot convert a contact with status " + status + " to CUSTOMER");
        }
        this.status = ContactStatus.CUSTOMER;
        this.linkedUserId = linkedUserId;
    }

    public void markLost() {
        if (status == ContactStatus.CUSTOMER) {
            throw new BusinessRuleException("Cannot mark an existing CUSTOMER as LOST");
        }
        this.status = ContactStatus.LOST;
    }

    private void requireStatus(ContactStatus required, String action) {
        if (status != required) {
            throw new BusinessRuleException(
                    "Cannot " + action + " a contact with status " + status + " (expected " + required + ")");
        }
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

    public String getSource() {
        return source;
    }

    public ContactStatus getStatus() {
        return status;
    }
}
