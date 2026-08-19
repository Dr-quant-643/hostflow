package com.hostflow.analytics.repository;

import com.hostflow.analytics.entity.PropertyOccupancySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * CRITICAL: every query method here MUST explicitly filter by tenantId — this view
 * carries NO Postgres RLS protection (materialized views cannot have RLS policies
 * applied to them directly in Postgres). This repository is the ONLY enforcement
 * point for tenant isolation on this data. Documented loudly here and in the
 * service layer to make sure this is never forgotten by a future contributor
 * copying the "just query the repository" pattern used everywhere else in this
 * codebase, where RLS silently protects them.
 */
public interface PropertyOccupancySummaryRepository extends JpaRepository<PropertyOccupancySummary, UUID> {

    @Query("SELECT p FROM PropertyOccupancySummary p WHERE p.tenantId = :tenantId ORDER BY p.totalRevenue DESC")
    List<PropertyOccupancySummary> findAllForTenant(@Param("tenantId") UUID tenantId);
}
