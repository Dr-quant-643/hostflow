package com.hostflow.mall.repository;

import com.hostflow.mall.entity.RetailUnit;
import com.hostflow.mall.entity.RetailUnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RetailUnitRepository extends JpaRepository<RetailUnit, UUID> {
    List<RetailUnit> findByPropertyId(UUID propertyId);
    long countByStatus(RetailUnitStatus status);
}
