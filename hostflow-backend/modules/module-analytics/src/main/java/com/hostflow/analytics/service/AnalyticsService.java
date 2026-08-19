package com.hostflow.analytics.service;

import com.hostflow.analytics.dto.MonthlyRevenueResponse;
import com.hostflow.analytics.dto.PropertyOccupancyResponse;
import com.hostflow.analytics.repository.MonthlyRevenueSummaryRepository;
import com.hostflow.analytics.repository.PropertyOccupancySummaryRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Every method here calls TenantContext.require() explicitly and passes it into
 * the repository query — this is NOT optional/defense-in-depth the way it is
 * elsewhere in the codebase (where RLS is the real enforcement and TenantScopedEntity's
 * @PrePersist assignment is the secondary layer). HERE, this explicit filtering IS
 * the primary and ONLY enforcement mechanism, since the underlying materialized
 * views have no RLS. Forgetting to call TenantContext.require() and pass it through
 * in any future method added to this service would be a real cross-tenant data leak,
 * not a redundant safety net.
 */
@Service
public class AnalyticsService {

    private final PropertyOccupancySummaryRepository occupancyRepository;
    private final MonthlyRevenueSummaryRepository revenueRepository;

    public AnalyticsService(PropertyOccupancySummaryRepository occupancyRepository,
                             MonthlyRevenueSummaryRepository revenueRepository) {
        this.occupancyRepository = occupancyRepository;
        this.revenueRepository = revenueRepository;
    }

    @Transactional(readOnly = true)
    public List<PropertyOccupancyResponse> getPropertyOccupancy() {
        return occupancyRepository.findAllForTenant(TenantContext.require()).stream()
                .map(PropertyOccupancyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        return revenueRepository.findAllForTenant(TenantContext.require()).stream()
                .map(MonthlyRevenueResponse::from)
                .toList();
    }
}
