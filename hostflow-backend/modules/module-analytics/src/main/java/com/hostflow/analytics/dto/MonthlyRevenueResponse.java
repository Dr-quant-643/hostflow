package com.hostflow.analytics.dto;

import com.hostflow.analytics.entity.MonthlyRevenueSummary;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyRevenueResponse(LocalDate month, BigDecimal invoicedTotal, BigDecimal paidTotal, Long invoiceCount) {

    public static MonthlyRevenueResponse from(MonthlyRevenueSummary summary) {
        return new MonthlyRevenueResponse(
                summary.getMonth(), summary.getInvoicedTotal(), summary.getPaidTotal(), summary.getInvoiceCount());
    }
}
