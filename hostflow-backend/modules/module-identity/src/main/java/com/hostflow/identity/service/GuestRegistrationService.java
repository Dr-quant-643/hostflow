package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.identity.dto.RegisterGuestRequest;
import com.hostflow.identity.entity.GuestProfile;
import com.hostflow.identity.keycloak.GuestKeycloakProvisioningService;
import com.hostflow.identity.repository.GuestProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * No TenantContext handling needed anywhere in this class — GuestProfile is a
 * plain BaseEntity, not TenantScopedEntity, so there is no @PrePersist tenant
 * requirement to satisfy, unlike OrganizationOnboardingService's User creation.
 * This is the direct structural benefit of keeping guests out of the tenant
 * model
 * entirely rather than forcing them into a synthetic "guest organization."
 */
@Service
public class GuestRegistrationService {

    private final GuestProfileRepository guestProfileRepository;
    private final GuestKeycloakProvisioningService keycloakProvisioningService;

    public GuestRegistrationService(GuestProfileRepository guestProfileRepository,
            GuestKeycloakProvisioningService keycloakProvisioningService) {
        this.guestProfileRepository = guestProfileRepository;
        this.keycloakProvisioningService = keycloakProvisioningService;
    }

    @Transactional
    public GuestProfile register(RegisterGuestRequest request) {
        if (guestProfileRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("An account with email '" + request.email() + "' already exists");
        }

        String keycloakId = keycloakProvisioningService.provisionGuest(
                request.email(), request.firstName(), request.lastName());

        GuestProfile profile = new GuestProfile(
                keycloakId, request.email(), request.firstName(), request.lastName(), request.phone());
        return guestProfileRepository.save(profile);
    }
}
