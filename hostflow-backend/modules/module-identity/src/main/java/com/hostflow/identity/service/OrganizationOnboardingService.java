package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.dto.OnboardOrganizationRequest;
import com.hostflow.identity.entity.Organization;
import com.hostflow.identity.entity.User;
import com.hostflow.identity.entity.UserRole;
import com.hostflow.identity.keycloak.KeycloakProvisioningService;
import com.hostflow.identity.messaging.TenantEventPublisher;
import com.hostflow.identity.repository.OrganizationRepository;
import com.hostflow.identity.repository.UserRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrganizationOnboardingService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final KeycloakProvisioningService keycloakProvisioningService;
    private final TenantEventPublisher tenantEventPublisher;

    public OrganizationOnboardingService(OrganizationRepository organizationRepository,
                                          UserRepository userRepository,
                                          KeycloakProvisioningService keycloakProvisioningService,
                                          TenantEventPublisher tenantEventPublisher) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.keycloakProvisioningService = keycloakProvisioningService;
        this.tenantEventPublisher = tenantEventPublisher;
    }

    @Transactional
    public Organization onboard(OnboardOrganizationRequest request) {
        if (organizationRepository.existsBySlug(request.organizationSlug())) {
            throw new BusinessRuleException("An organization with slug '" + request.organizationSlug() + "' already exists");
        }
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new BusinessRuleException("A user with email '" + request.adminEmail() + "' already exists");
        }

        Organization organization = new Organization(request.organizationName(), request.organizationSlug(), request.primaryProduct());
        organization = organizationRepository.save(organization);

        TenantContext.set(organization.getId());
        try {
            UserRole ownerRole = request.primaryProduct() == com.hostflow.identity.entity.OrganizationProduct.XANUOS
                    ? UserRole.XANUOS_OWNER
                    : UserRole.NAZILCO_CUSTOMER;

            String keycloakUserId = keycloakProvisioningService.provisionUser(
                    organization.getId(),
                    request.adminEmail(),
                    request.adminFirstName(),
                    request.adminLastName(),
                    List.of(ownerRole.name().toLowerCase()),
                    List.of(request.primaryProduct().name())
            );

            User adminUser = new User(keycloakUserId, request.adminEmail(),
                    request.adminFirstName(), request.adminLastName(), Set.of(ownerRole));
            userRepository.save(adminUser);

            tenantEventPublisher.created(organization);
        } finally {
            TenantContext.clear();
        }

        return organization;
    }

    @Transactional
    public Organization rename(UUID orgId, String newName) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgId));
        organization.rename(newName);
        tenantEventPublisher.updated(organization);
        return organization;
    }
}
