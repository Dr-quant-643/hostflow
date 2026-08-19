package com.hostflow.billing.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceEntityTest {

    @Test
    void constructor_rejectsZeroOrNegativeAmount() {
        assertThatThrownBy(() -> new Invoice(null, UUID.randomUUID(), BigDecimal.ZERO, LocalDate.now().plusDays(30)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void newInvoice_startsAsDraft() {
        Invoice invoice = new Invoice(null, UUID.randomUUID(), BigDecimal.valueOf(500), LocalDate.now().plusDays(30));

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
    }

    @Test
    void fullLifecycle_draftToIssuedToPaid() {
        Invoice invoice = new Invoice(null, UUID.randomUUID(), BigDecimal.valueOf(500), LocalDate.now().plusDays(30));

        invoice.issue();
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.ISSUED);

        invoice.markPaid();
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
    }

    @Test
    void markPaid_alsoWorksFromOverdue() {
        Invoice invoice = new Invoice(null, UUID.randomUUID(), BigDecimal.valueOf(500), LocalDate.now().plusDays(30));
        invoice.issue();
        invoice.markOverdue();

        invoice.markPaid();

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
    }

    @Test
    void voidInvoice_onPaidInvoice_throws() {
        Invoice invoice = new Invoice(null, UUID.randomUUID(), BigDecimal.valueOf(500), LocalDate.now().plusDays(30));
        invoice.issue();
        invoice.markPaid();

        assertThatThrownBy(invoice::voidInvoice)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot void a PAID invoice");
    }
}
