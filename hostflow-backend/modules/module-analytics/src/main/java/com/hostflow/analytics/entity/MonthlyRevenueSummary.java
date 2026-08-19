package com.hostflow.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "mv_monthly_revenue_summary")
public class MonthlyRevenueSummary {

    /**
     * Composite natural key (tenant_id + month) has no single UUID id in the
     * underlying view, so this synthetic id is derived at query time
     * (tenant_id::text || '-' || month) in the V12 migration's view definition,
     * purely to satisfy JPA's @Id requirement — never used for lookups by callers.
     */
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "month")
    private LocalDate month;

    @Column(name = "invoiced_total")
    private BigDecimal invoicedTotal;

    @Column(name = "paid_total")
    private BigDecimal paidTotal;

    @Column(name = "invoice_count")
    private Long invoiceCount;

    protected MonthlyRevenueSummary() {
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public LocalDate getMonth() {
        return month;
    }

    public BigDecimal getInvoicedTotal() {
        return invoicedTotal;
    }

    public BigDecimal getPaidTotal() {
        return paidTotal;
    }

    public Long getInvoiceCount() {
        return invoiceCount;
    }
}
