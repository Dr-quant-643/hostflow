package com.hostflow.crm.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.crm.dto.CreateContactRequest;
import com.hostflow.crm.entity.Contact;
import com.hostflow.crm.entity.Interaction;
import com.hostflow.crm.entity.InteractionType;
import com.hostflow.crm.entity.ContactStatus;
import com.hostflow.crm.repository.ContactRepository;
import com.hostflow.crm.repository.InteractionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final InteractionRepository interactionRepository;

    public ContactService(ContactRepository contactRepository, InteractionRepository interactionRepository) {
        this.contactRepository = contactRepository;
        this.interactionRepository = interactionRepository;
    }

    @Transactional(readOnly = true)
    public List<Contact> list(ContactStatus status, int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset / Math.max(limit, 1), limit);
        return status != null
                ? contactRepository.findByStatus(status, pageRequest).getContent()
                : contactRepository.findAll(pageRequest).getContent();
    }

    @Transactional(readOnly = true)
    public List<Interaction> listInteractions(UUID contactId) {
        getById(contactId);
        return interactionRepository.findByContactIdOrderByCreatedAtDesc(contactId);
    }

    @Transactional
    public Contact create(CreateContactRequest request) {
        Contact contact = new Contact(request.fullName(), request.email(), request.phone(), request.source());
        contact = contactRepository.save(contact);

        // Every new contact gets a SYSTEM_EVENT interaction automatically — the
        // creation itself is the first entry in the interaction history, so the
        // timeline is complete from the very start rather than beginning empty.
        interactionRepository.save(new Interaction(contact.getId(), contact.getId(),
                InteractionType.SYSTEM_EVENT, "Contact created via source: " + request.source()));

        return contact;
    }

    @Transactional(readOnly = true)
    public Contact getById(UUID contactId) {
        return contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", contactId));
    }

    @Transactional
    public Contact qualify(UUID contactId) {
        Contact contact = getById(contactId);
        contact.qualify();
        return contact;
    }

    @Transactional
    public Interaction logInteraction(UUID contactId, UUID loggedByUserId, InteractionType type, String notes) {
        // Verifies the contact exists (RLS-scoped) before logging, so an interaction
        // can never be attached to a contact from another tenant even if a caller
        // somehow supplied a foreign UUID.
        getById(contactId);
        return interactionRepository.save(new Interaction(contactId, loggedByUserId, type, notes));
    }
}
