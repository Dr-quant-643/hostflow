package com.hostflow.crm.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "crm_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CrmContact extends TenantScopedEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(name = "full_name", length = 500)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactStatus status = ContactStatus.ACTIVE;

    @Column(name = "assigned_to_user_id")
    private UUID assignedToUserId;

    @Column(name = "last_contacted_at")
    private LocalDateTime lastContactedAt;

    public enum ContactStatus {
        ACTIVE,
        INACTIVE,
        LEAD,
        CUSTOMER
    }

    @PrePersist
    @PreUpdate
    private void updateFullName() {
        if (firstName != null && lastName != null) {
            this.fullName = firstName + " " + lastName;
        }
    }

    public String getFullName() {
        if (fullName == null && firstName != null && lastName != null) {
            fullName = firstName + " " + lastName;
        }
        return fullName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }
}
