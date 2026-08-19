package com.hostflow.analytics.repository;

import com.hostflow.analytics.entity.MonthlyRevenueSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/** Same critical tenant-filtering requirement as PropertyOccupancySummaryRepository. */
public interface MonthlyRevenueSummaryRepository extends JpaRepository<MonthlyRevenueSummary, String> {

    @Query("SELECT m FROM MonthlyRevenueSummary m WHERE m.tenantId = :tenantId ORDER BY m.month DESC")
    List<MonthlyRevenueSummary> findAllForTenant(@Param("tenantId") UUID tenantId);
}
