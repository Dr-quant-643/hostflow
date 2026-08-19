package com.hostflow.platformadmin.repository;

import com.hostflow.platformadmin.entity.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, UUID> {
    Page<AuditLogEntry> findByActorTenantId(UUID actorTenantId, Pageable pageable);
    Page<AuditLogEntry> findByResourceType(String resourceType, Pageable pageable);
    Page<AuditLogEntry> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
