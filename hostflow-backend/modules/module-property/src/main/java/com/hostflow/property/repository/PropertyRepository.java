package com.hostflow.property.repository;

import com.hostflow.property.entity.Property;
import com.hostflow.property.entity.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * All queries implicitly tenant-scoped via RLS (V6 migration) — no manual
 * "AND tenant_id = ?" needed anywhere here, same pattern as module-identity's
 * UserRepository.
 */
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);
    Page<Property> findByOwnerUserId(UUID ownerUserId, Pageable pageable);
}
