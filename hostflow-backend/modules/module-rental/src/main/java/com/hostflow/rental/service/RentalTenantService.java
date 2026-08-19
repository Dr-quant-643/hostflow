package com.hostflow.rental.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.rental.dto.CreateRentalTenantRequest;
import com.hostflow.rental.entity.RentalTenant;
import com.hostflow.rental.repository.RentalTenantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RentalTenantService {

    private final RentalTenantRepository repository;

    public RentalTenantService(RentalTenantRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RentalTenant> list(int limit, int offset) {
        return repository.findAll(PageRequest.of(offset / Math.max(limit, 1), limit)).getContent();
    }

    @Transactional
    public RentalTenant create(CreateRentalTenantRequest request) {
        return repository.save(new RentalTenant(request.fullName(), request.email(), request.phone()));
    }

    @Transactional(readOnly = true)
    public RentalTenant getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RentalTenant", id));
    }

    /**
     * Called from RentalTenantLinkOrchestrator (app module) once TenantContext has
     * already been set to the tenant that owns this record — RLS then scopes
     * getById() correctly. Relies on dirty checking, no explicit save() needed.
     */
    @Transactional
    public RentalTenant linkToUser(UUID id, UUID userId) {
        RentalTenant tenant = getById(id);
        tenant.linkToUser(userId);
        return tenant;
    }
}
