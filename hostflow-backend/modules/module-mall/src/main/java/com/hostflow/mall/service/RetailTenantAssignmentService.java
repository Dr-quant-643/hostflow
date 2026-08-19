package com.hostflow.mall.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.mall.dto.AssignRetailTenantRequest;
import com.hostflow.mall.entity.RetailTenant;
import com.hostflow.mall.entity.RetailUnit;
import com.hostflow.mall.repository.RetailTenantRepository;
import com.hostflow.mall.repository.RetailUnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetailTenantAssignmentService {

    private final RetailUnitRepository unitRepository;
    private final RetailTenantRepository tenantRepository;

    public RetailTenantAssignmentService(RetailUnitRepository unitRepository, RetailTenantRepository tenantRepository) {
        this.unitRepository = unitRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public RetailTenant assign(AssignRetailTenantRequest request) {
        RetailUnit unit = unitRepository.findById(request.retailUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("RetailUnit", request.retailUnitId()));
        unit.markOccupied();

        RetailTenant tenant = new RetailTenant(request.retailUnitId(), request.businessName(),
                request.contactEmail(), request.contactPhone(), request.monthlyRent(), request.revenueSharePercent());
        return tenantRepository.save(tenant);
    }
}
