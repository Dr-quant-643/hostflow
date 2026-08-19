package com.hostflow.crm.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactEntityTest {

    @Test
    void newContact_startsAsLead() {
        Contact contact = new Contact("Jane Doe", "jane@example.com", "+254700000000", "website_form");

        assertThat(contact.getStatus()).isEqualTo(ContactStatus.LEAD);
    }

    @Test
    void qualify_transitionsFromLeadToQualified() {
        Contact contact = new Contact("Jane Doe", "jane@example.com", null, "referral");

        contact.qualify();

        assertThat(contact.getStatus()).isEqualTo(ContactStatus.QUALIFIED);
    }

    @Test
    void qualify_throwsWhenNotInLeadStatus() {
        Contact contact = new Contact("Jane Doe", "jane@example.com", null, "referral");
        contact.qualify();

        assertThatThrownBy(contact::qualify)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("expected LEAD");
    }

    @Test
    void convertToCustomer_setsLinkedUserId() {
        Contact contact = new Contact("Jane Doe", "jane@example.com", null, "referral");
        UUID userId = UUID.randomUUID();

        contact.convertToCustomer(userId);

        assertThat(contact.getStatus()).isEqualTo(ContactStatus.CUSTOMER);
        assertThat(contact.getLinkedUserId()).isEqualTo(userId);
    }

    @Test
    void markLost_onExistingCustomer_throws() {
        Contact contact = new Contact("Jane Doe", "jane@example.com", null, "referral");
        contact.convertToCustomer(UUID.randomUUID());

        assertThatThrownBy(contact::markLost)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("existing CUSTOMER");
    }

    @Test
    void convertToCustomer_onLostContact_throws() {
        Contact contact = new Contact("Jane Doe", "jane@example.com", null, "referral");
        contact.markLost();

        assertThatThrownBy(() -> contact.convertToCustomer(UUID.randomUUID()))
                .isInstanceOf(BusinessRuleException.class);
    }
}
