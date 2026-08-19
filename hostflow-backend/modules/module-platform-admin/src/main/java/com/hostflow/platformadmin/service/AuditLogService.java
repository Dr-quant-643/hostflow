package com.hostflow.platformadmin.service;

import com.hostflow.platformadmin.entity.AuditLogEntry;
import com.hostflow.platformadmin.repository.AuditLogEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Intended to be called from OTHER modules as a Java dependency (once
 * module-platform-admin is added to their pom.xml) whenever a security-relevant
 * or business-critical action occurs — role changes, org onboarding, invoice
 * voiding, etc. NOT yet wired into any existing module's write paths (that's a
 * genuinely large follow-up: auditing every sensitive action across 15+ modules)
 * — this module ships the SERVICE and the query/read side; call-site instrumentation
 * across the rest of the codebase is flagged explicitly as unfinished below.
 */
@Service
public class AuditLogService {

    private final AuditLogEntryRepository repository;

    public AuditLogService(AuditLogEntryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(UUID actorUserId, UUID actorTenantId, String action, String resourceType,
                        String resourceId, String detail) {
        repository.save(new AuditLogEntry(actorUserId, actorTenantId, action, resourceType, resourceId, detail));
    }

    @Transactional(readOnly = true)
    public Page<AuditLogEntry> listAll(int limit, int offset) {
        return repository.findAllByOrderByCreatedAtDesc(PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional(readOnly = true)
    public Page<AuditLogEntry> listByTenant(UUID tenantId, int limit, int offset) {
        return repository.findByActorTenantId(tenantId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }
}
