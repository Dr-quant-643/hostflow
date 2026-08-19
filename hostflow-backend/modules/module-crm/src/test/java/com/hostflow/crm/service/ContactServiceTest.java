package com.hostflow.crm.service;

import com.hostflow.crm.dto.CreateContactRequest;
import com.hostflow.crm.entity.Contact;
import com.hostflow.crm.entity.Interaction;
import com.hostflow.crm.entity.InteractionType;
import com.hostflow.crm.repository.ContactRepository;
import com.hostflow.crm.repository.InteractionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private InteractionRepository interactionRepository;

    private ContactService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new ContactService(contactRepository, interactionRepository);
    }

    @Test
    void create_savesContactAndLogsSystemEventInteraction() {
        CreateContactRequest request = new CreateContactRequest("Jane Doe", "jane@example.com", null, "landing_page");
        when(contactRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Contact result = service.create(request);

        assertThat(result.getFullName()).isEqualTo("Jane Doe");
        verify(interactionRepository, times(1)).save(any(Interaction.class));
    }

    @Test
    void logInteraction_verifiesContactExistsBeforeLogging() {
        UUID contactId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Contact existing = new Contact("Jane Doe", "jane@example.com", null, "referral");
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existing));
        when(interactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.logInteraction(contactId, userId, InteractionType.CALL, "Discussed pricing");

        verify(contactRepository).findById(contactId);
        verify(interactionRepository).save(any(Interaction.class));
    }
}
