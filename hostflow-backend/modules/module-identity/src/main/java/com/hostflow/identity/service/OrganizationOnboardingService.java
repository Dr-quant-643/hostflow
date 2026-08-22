package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.dto.OnboardOrganizationRequest;
import com.hostflow.identity.dto.RegisterHostRequest;
import com.hostflow.identity.entity.Organization;
import com.hostflow.identity.entity.OrganizationProduct;
import com.hostflow.identity.entity.User;
import com.hostflow.identity.entity.UserRole;
import com.hostflow.identity.keycloak.HostKeycloakProvisioningService;
import com.hostflow.identity.keycloak.KeycloakProvisioningService;
import com.hostflow.identity.messaging.TenantEventPublisher;
import com.hostflow.identity.repository.OrganizationRepository;
import com.hostflow.identity.repository.UserRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class OrganizationOnboardingService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final KeycloakProvisioningService keycloakProvisioningService;
    private final HostKeycloakProvisioningService hostKeycloakProvisioningService;
    private final TenantEventPublisher tenantEventPublisher;

    public OrganizationOnboardingService(OrganizationRepository organizationRepository,
                                          UserRepository userRepository,
                                          KeycloakProvisioningService keycloakProvisioningService,
                                          HostKeycloakProvisioningService hostKeycloakProvisioningService,
                                          TenantEventPublisher tenantEventPublisher) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.keycloakProvisioningService = keycloakProvisioningService;
        this.hostKeycloakProvisioningService = hostKeycloakProvisioningService;
        this.tenantEventPublisher = tenantEventPublisher;
    }

    /**
     * Self-service counterpart to onboard() — no PLATFORM_ADMIN in the loop,
     * called from the anonymous HostSelfSignupController. Always creates a
     * XANUOS-primary organization with the caller as XANUOS_OWNER; onboarding
     * a NAZILCO-primary org, or adding staff to an existing one, still goes
     * through the admin-gated onboard() path. The slug is derived from the
     * business name rather than asked for -- a brand-new host has no reason
     * to already know HostFlow's URL-safe-slug convention.
     */
    @Transactional
    public Organization selfSignup(RegisterHostRequest request) {
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new BusinessRuleException("A user with email '" + request.adminEmail() + "' already exists");
        }

        String slug = generateUniqueSlug(request.organizationName());
        Organization organization = new Organization(request.organizationName(), slug, OrganizationProduct.XANUOS);
        organization = organizationRepository.save(organization);

        TenantContext.set(organization.getId());
        try {
            String keycloakUserId = hostKeycloakProvisioningService.provisionHostOwner(
                    organization.getId(),
                    request.adminEmail(),
                    request.adminFirstName(),
                    request.adminLastName(),
                    request.password()
            );

            User ownerUser = new User(keycloakUserId, request.adminEmail(),
                    request.adminFirstName(), request.adminLastName(), Set.of(UserRole.XANUOS_OWNER));
            userRepository.save(ownerUser);

            tenantEventPublisher.created(organization);
        } finally {
            TenantContext.clear();
        }

        return organization;
    }

    /**
     * Counterpart to selfSignup() for a caller who already has a Keycloak
     * identity but no workspace -- the "Continue with Google" path, where
     * Keycloak's first-broker-login already created the account with no
     * tenant_id/product_scope. Attaches an org to that EXISTING identity
     * instead of creating a new one (selfSignup() would collide on
     * email/username).
     */
    @Transactional
    public Organization claimWorkspace(String keycloakUserId, String email, String firstName, String lastName,
                                        String organizationName) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("A workspace already exists for '" + email + "'");
        }

        String slug = generateUniqueSlug(organizationName);
        Organization organization = new Organization(organizationName, slug, OrganizationProduct.XANUOS);
        organization = organizationRepository.save(organization);

        TenantContext.set(organization.getId());
        try {
            hostKeycloakProvisioningService.attachExistingUserToOrganization(keycloakUserId, organization.getId());

            User ownerUser = new User(keycloakUserId, email, firstName, lastName, Set.of(UserRole.XANUOS_OWNER));
            userRepository.save(ownerUser);

            tenantEventPublisher.created(organization);
        } finally {
            TenantContext.clear();
        }

        return organization;
    }

    private String generateUniqueSlug(String organizationName) {
        String base = organizationName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isEmpty()) {
            base = "org";
        }

        String candidate = base;
        int suffix = 2;
        while (organizationRepository.existsBySlug(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
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
