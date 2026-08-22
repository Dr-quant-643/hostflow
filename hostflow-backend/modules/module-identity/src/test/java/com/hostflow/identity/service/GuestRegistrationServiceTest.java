package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.identity.dto.RegisterGuestRequest;
import com.hostflow.identity.entity.GuestProfile;
import com.hostflow.identity.keycloak.GuestKeycloakProvisioningService;
import com.hostflow.identity.repository.GuestProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuestRegistrationServiceTest {

    @Mock
    private GuestProfileRepository guestProfileRepository;
    @Mock
    private GuestKeycloakProvisioningService keycloakProvisioningService;

    private GuestRegistrationService service;

    private final RegisterGuestRequest request = new RegisterGuestRequest("Amina", "Njoroge", "amina@example.com",
            "+254700000000", "correct-horse-battery-staple");

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new GuestRegistrationService(guestProfileRepository, keycloakProvisioningService);
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(guestProfileRepository.existsByEmail("amina@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("amina@example.com");
    }

    @Test
    void register_provisionsKeycloakUser_thenSavesProfileWithReturnedId() {
        when(guestProfileRepository.existsByEmail(anyString())).thenReturn(false);
        when(keycloakProvisioningService.provisionGuest("amina@example.com", "Amina", "Njoroge",
                "correct-horse-battery-staple"))
                .thenReturn("kc-guest-123");
        when(guestProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GuestProfile result = service.register(request);

        assertThat(result.getKeycloakId()).isEqualTo("kc-guest-123");
        assertThat(result.getEmail()).isEqualTo("amina@example.com");
    }

    @Test
    void claimProfile_rejectsIfProfileAlreadyExistsForEmail() {
        when(guestProfileRepository.existsByEmail("amina@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.claimProfile("kc-user-1", "amina@example.com", "Amina", "Njoroge"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("amina@example.com");
    }

    @Test
    void claimProfile_attachesExistingIdentityRatherThanCreatingNewOne() {
        when(guestProfileRepository.existsByEmail(anyString())).thenReturn(false);
        when(guestProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GuestProfile result = service.claimProfile("kc-user-1", "amina@example.com", "Amina", "Njoroge");

        assertThat(result.getKeycloakId()).isEqualTo("kc-user-1");
        assertThat(result.getEmail()).isEqualTo("amina@example.com");
        org.mockito.Mockito.verify(keycloakProvisioningService).attachExistingUserAsGuest("kc-user-1");
    }
}
