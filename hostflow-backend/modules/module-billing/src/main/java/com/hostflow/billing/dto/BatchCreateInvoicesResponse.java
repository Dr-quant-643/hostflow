package com.hostflow.billing.dto;

import java.util.List;

public record BatchCreateInvoicesResponse(int totalRequested, int succeeded, int failed, List<BatchInvoiceResult> results) {

    public static BatchCreateInvoicesResponse from(List<BatchInvoiceResult> results) {
        long succeeded = results.stream().filter(BatchInvoiceResult::success).count();
        return new BatchCreateInvoicesResponse(results.size(), (int) succeeded, results.size() - (int) succeeded, results);
    }
}
