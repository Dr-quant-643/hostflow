package com.hostflow.crm.dto;

import com.hostflow.crm.entity.Contact;

import java.util.UUID;

public record ContactResponse(UUID id, String fullName, String email, String phone, String source, String status) {

    public static ContactResponse from(Contact contact) {
        return new ContactResponse(
                contact.getId(), contact.getFullName(), contact.getEmail(),
                contact.getPhone(), contact.getSource(), contact.getStatus().name());
    }
}
