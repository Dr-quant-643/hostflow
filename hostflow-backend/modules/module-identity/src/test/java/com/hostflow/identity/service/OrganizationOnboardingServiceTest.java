package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.identity.dto.OnboardOrganizationRequest;
import com.hostflow.identity.dto.RegisterHostRequest;
import com.hostflow.identity.entity.Organization;
import com.hostflow.identity.entity.OrganizationProduct;
import com.hostflow.identity.keycloak.HostKeycloakProvisioningService;
import com.hostflow.identity.keycloak.KeycloakProvisioningService;
import com.hostflow.identity.messaging.TenantEventPublisher;
import com.hostflow.identity.repository.OrganizationRepository;
import com.hostflow.identity.repository.UserRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationOnboardingServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KeycloakProvisioningService keycloakProvisioningService;
    @Mock
    private HostKeycloakProvisioningService hostKeycloakProvisioningService;
    @Mock
    private TenantEventPublisher tenantEventPublisher;

    private OrganizationOnboardingService service;

    private final OnboardOrganizationRequest request = new OnboardOrganizationRequest(
            "Acme Properties", "acme-properties", OrganizationProduct.XANUOS,
            "Jane", "Doe", "jane@acme.com"
    );

    private final RegisterHostRequest selfSignupRequest = new RegisterHostRequest(
            "Acme Properties", "Jane", "Doe", "jane@acme.com", "supersecret1"
    );

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new OrganizationOnboardingService(
                organizationRepository, userRepository, keycloakProvisioningService,
                hostKeycloakProvisioningService, tenantEventPublisher);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void onboard_rejectsDuplicateSlug() {
        when(organizationRepository.existsBySlug("acme-properties")).thenReturn(true);

        assertThatThrownBy(() -> service.onboard(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("acme-properties");
    }

    @Test
    void onboard_rejectsDuplicateAdminEmail() {
        when(organizationRepository.existsBySlug(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("jane@acme.com")).thenReturn(true);

        assertThatThrownBy(() -> service.onboard(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("jane@acme.com");
    }

    @Test
    void onboard_createsOrganizationThenProvisionsKeycloakUser_inCorrectOrder() {
        UUID orgId = UUID.randomUUID();
        Organization savedOrg = new Organization("Acme Properties", "acme-properties", OrganizationProduct.XANUOS);
        // Simulate the id being assigned on save, as JPA would.
        Organization orgWithId = spyWithId(savedOrg, orgId);

        when(organizationRepository.existsBySlug(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(organizationRepository.save(any())).thenReturn(orgWithId);
        when(keycloakProvisioningService.provisionUser(any(), anyString(), anyString(), anyString(), anyList(), anyList()))
                .thenReturn("keycloak-user-123");

        Organization result = service.onboard(request);

        assertThat(result.getId()).isEqualTo(orgId);
        verify(keycloakProvisioningService).provisionUser(
                org.mockito.ArgumentMatchers.eq(orgId), anyString(), anyString(), anyString(), anyList(), anyList());
        verify(userRepository).save(any());
        // TenantContext must be cleared after onboarding completes — no leakage into
        // whatever request/thread runs next.
        assertThat(TenantContext.isSet()).isFalse();
    }

    private Organization spyWithId(Organization organization, UUID id) {
        Organization spy = org.mockito.Mockito.spy(organization);
        when(spy.getId()).thenReturn(id);
        return spy;
    }

    @Test
    void selfSignup_rejectsDuplicateAdminEmail() {
        when(userRepository.existsByEmail("jane@acme.com")).thenReturn(true);

        assertThatThrownBy(() -> service.selfSignup(selfSignupRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("jane@acme.com");
    }

    @Test
    void selfSignup_derivesSlugFromOrganizationNameAndDedupesOnCollision() {
        UUID orgId = UUID.randomUUID();
        Organization savedOrg = new Organization("Acme Properties", "acme-properties-2", OrganizationProduct.XANUOS);
        Organization orgWithId = spyWithId(savedOrg, orgId);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // The natural slug is already taken -- must fall back to "-2".
        when(organizationRepository.existsBySlug("acme-properties")).thenReturn(true);
        when(organizationRepository.existsBySlug("acme-properties-2")).thenReturn(false);
        when(organizationRepository.save(any())).thenReturn(orgWithId);
        when(hostKeycloakProvisioningService.provisionHostOwner(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("keycloak-user-456");

        Organization result = service.selfSignup(selfSignupRequest);

        assertThat(result.getId()).isEqualTo(orgId);
        verify(organizationRepository).save(org.mockito.ArgumentMatchers.argThat(
                org -> org.getSlug().equals("acme-properties-2")
                        && org.getPrimaryProduct() == OrganizationProduct.XANUOS));
        verify(hostKeycloakProvisioningService).provisionHostOwner(
                org.mockito.ArgumentMatchers.eq(orgId), anyString(), anyString(), anyString(), anyString());
        verify(userRepository).save(any());
        assertThat(TenantContext.isSet()).isFalse();
    }

    @Test
    void claimWorkspace_rejectsIfWorkspaceAlreadyExistsForEmail() {
        when(userRepository.existsByEmail("jane@acme.com")).thenReturn(true);

        assertThatThrownBy(() -> service.claimWorkspace(
                "kc-user-1", "jane@acme.com", "Jane", "Doe", "Acme Properties"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("jane@acme.com");
    }

    @Test
    void claimWorkspace_attachesExistingIdentityRatherThanCreatingNewOne() {
        UUID orgId = UUID.randomUUID();
        Organization savedOrg = new Organization("Acme Properties", "acme-properties", OrganizationProduct.XANUOS);
        Organization orgWithId = spyWithId(savedOrg, orgId);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(organizationRepository.existsBySlug(anyString())).thenReturn(false);
        when(organizationRepository.save(any())).thenReturn(orgWithId);

        Organization result = service.claimWorkspace("kc-user-1", "jane@acme.com", "Jane", "Doe", "Acme Properties");

        assertThat(result.getId()).isEqualTo(orgId);
        verify(hostKeycloakProvisioningService).attachExistingUserToOrganization("kc-user-1", orgId);
        verify(userRepository).save(any());
        assertThat(TenantContext.isSet()).isFalse();
    }
}
