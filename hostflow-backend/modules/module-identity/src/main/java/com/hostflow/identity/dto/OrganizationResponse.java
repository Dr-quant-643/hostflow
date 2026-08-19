package com.hostflow.identity.dto;

import com.hostflow.identity.entity.Organization;

import java.util.UUID;

public record OrganizationResponse(UUID id, String name, String slug, String primaryProduct, boolean active) {

    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getPrimaryProduct().name(),
                organization.isActive()
        );
    }
}
