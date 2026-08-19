package com.hostflow.analytics.service;

import com.hostflow.analytics.entity.MonthlyRevenueSummary;
import com.hostflow.analytics.entity.PropertyOccupancySummary;
import com.hostflow.analytics.repository.MonthlyRevenueSummaryRepository;
import com.hostflow.analytics.repository.PropertyOccupancySummaryRepository;
import com.hostflow.common.exception.TenantContextMissingException;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private PropertyOccupancySummaryRepository occupancyRepository;
    @Mock
    private MonthlyRevenueSummaryRepository revenueRepository;

    private AnalyticsService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new AnalyticsService(occupancyRepository, revenueRepository);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    /**
     * THE most important test in this module: proves that calling either analytics
     * method WITHOUT a tenant context set fails loudly (TenantContextMissingException)
     * rather than silently querying with a null tenantId — which, given these views
     * have no RLS, could otherwise return every tenant's data. This test is the
     * concrete verification of the "primary enforcement mechanism" claim made in
     * AnalyticsService's javadoc and the Flyway migration's RLS-exception comment.
     */
    @Test
    void getPropertyOccupancy_withNoTenantContext_throwsRatherThanQueryingWithNullTenant() {
        assertThatThrownBy(() -> service.getPropertyOccupancy())
                .isInstanceOf(TenantContextMissingException.class);
    }

    @Test
    void getMonthlyRevenue_withNoTenantContext_throwsRatherThanQueryingWithNullTenant() {
        assertThatThrownBy(() -> service.getMonthlyRevenue())
                .isInstanceOf(TenantContextMissingException.class);
    }

    @Test
    void getPropertyOccupancy_withTenantContextSet_passesExactTenantIdToRepository() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);
        when(occupancyRepository.findAllForTenant(tenantId)).thenReturn(List.of());

        service.getPropertyOccupancy();

        verify(occupancyRepository).findAllForTenant(tenantId);
    }

    @Test
    void getMonthlyRevenue_withTenantContextSet_passesExactTenantIdToRepository() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);
        when(revenueRepository.findAllForTenant(tenantId)).thenReturn(List.of());

        service.getMonthlyRevenue();

        verify(revenueRepository).findAllForTenant(tenantId);
    }
}
