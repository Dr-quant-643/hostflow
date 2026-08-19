package com.hostflow.billing.service;

import com.hostflow.billing.dto.BatchInvoiceResult;
import com.hostflow.billing.dto.CreateInvoiceRequest;
import com.hostflow.billing.entity.Invoice;
import com.hostflow.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceBatchTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    private InvoiceService service;
    private InvoiceRowWriter invoiceRowWriter;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // InvoiceRowWriter is constructed directly (not mocked) here, sharing the
        // same mocked InvoiceRepository — this exercises writeRow()'s real logic
        // while still avoiding any real database. Note this test still does NOT
        // exercise real Spring proxying/REQUIRES_NEW behavior (that requires a full
        // Spring context, which is exactly what app module's future
        // BatchTransactionIsolationIT would need to add for full confidence) — it
        // only verifies the row-iteration/success-failure-reporting LOGIC is correct.
        invoiceRowWriter = new InvoiceRowWriter(invoiceRepository);
        service = new InvoiceService(invoiceRepository, invoiceRowWriter);
    }

    @Test
    void createBatch_allValidRows_allSucceed() {
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<CreateInvoiceRequest> requests = List.of(
                new CreateInvoiceRequest(null, UUID.randomUUID(), BigDecimal.valueOf(100), LocalDate.now().plusDays(10)),
                new CreateInvoiceRequest(null, UUID.randomUUID(), BigDecimal.valueOf(200), LocalDate.now().plusDays(15))
        );

        List<BatchInvoiceResult> results = service.createBatch(requests);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(BatchInvoiceResult::success);
    }

    @Test
    void createBatch_oneInvalidRow_doesNotFailOthers_andReportsIndexCorrectly() {
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<CreateInvoiceRequest> requests = List.of(
                new CreateInvoiceRequest(null, UUID.randomUUID(), BigDecimal.valueOf(100), LocalDate.now().plusDays(10)),
                new CreateInvoiceRequest(null, UUID.randomUUID(), BigDecimal.ZERO, LocalDate.now().plusDays(10)),
                new CreateInvoiceRequest(null, UUID.randomUUID(), BigDecimal.valueOf(300), LocalDate.now().plusDays(10))
        );

        List<BatchInvoiceResult> results = service.createBatch(requests);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).success()).isTrue();
        assertThat(results.get(0).index()).isEqualTo(0);
        assertThat(results.get(1).success()).isFalse();
        assertThat(results.get(1).index()).isEqualTo(1);
        assertThat(results.get(1).errorMessage()).contains("greater than zero");
        assertThat(results.get(2).success()).isTrue();
        assertThat(results.get(2).index()).isEqualTo(2);
    }
}
