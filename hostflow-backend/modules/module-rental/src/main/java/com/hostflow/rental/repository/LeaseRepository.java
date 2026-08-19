package com.hostflow.rental.repository;

import com.hostflow.rental.entity.Lease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaseRepository extends JpaRepository<Lease, UUID> {
    Page<Lease> findByPropertyId(UUID propertyId, Pageable pageable);
    List<Lease> findByTenantIdRef(UUID tenantIdRef);
}
