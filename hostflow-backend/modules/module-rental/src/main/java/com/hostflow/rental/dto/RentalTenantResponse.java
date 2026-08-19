package com.hostflow.rental.dto;

import com.hostflow.rental.entity.RentalTenant;

import java.util.UUID;

public record RentalTenantResponse(UUID id, String fullName, String email, String phone) {

    public static RentalTenantResponse from(RentalTenant tenant) {
        return new RentalTenantResponse(tenant.getId(), tenant.getFullName(), tenant.getEmail(), tenant.getPhone());
    }
}
