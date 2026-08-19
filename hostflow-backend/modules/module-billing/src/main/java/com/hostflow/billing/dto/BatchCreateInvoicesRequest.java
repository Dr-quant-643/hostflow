package com.hostflow.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Implements the Batch API decision from the master architecture doc's open items:
 * "REST endpoints that accept an array/CSV payload, validate row-by-row, and return
 * a per-row success/failure report, processed synchronously for small batches and
 * pushed through the RabbitMQ pipeline for large ones."
 *
 * This module implements the SYNCHRONOUS small-batch path only (capped at 100 rows
 * via @Size) — the RabbitMQ-based large-batch path is intentionally NOT built here,
 * flagged explicitly in the module report below as a follow-up once a real bulk
 * use case (e.g. CSV import of hundreds of invoices) actually appears.
 */
public record BatchCreateInvoicesRequest(
        @NotEmpty @Size(max = 100, message = "Batch requests are capped at 100 items; use the async path for larger imports (not yet implemented)")
        @Valid List<CreateInvoiceRequest> invoices
) {
}
