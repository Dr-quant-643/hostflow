package com.hostflow.identity.repository;

import com.hostflow.identity.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Deliberately NOT tenant-filtered (Organization extends BaseEntity, not
 * TenantScopedEntity, and has no RLS policy) — organization lookup by slug happens
 * BEFORE a tenant context exists (e.g. resolving which org a login belongs to).
 */
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
