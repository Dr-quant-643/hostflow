package com.hostflow.billing.dto;

import java.util.UUID;

/**
 * One entry per input row. index correlates back to the row's position in the
 * original request array, since a failed row has no invoiceId to identify it by.
 */
public record BatchInvoiceResult(int index, boolean success, UUID invoiceId, String errorMessage) {

    public static BatchInvoiceResult success(int index, UUID invoiceId) {
        return new BatchInvoiceResult(index, true, invoiceId, null);
    }

    public static BatchInvoiceResult failure(int index, String errorMessage) {
        return new BatchInvoiceResult(index, false, null, errorMessage);
    }
}
