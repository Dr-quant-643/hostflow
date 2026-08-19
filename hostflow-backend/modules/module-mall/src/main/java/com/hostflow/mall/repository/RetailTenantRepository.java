package com.hostflow.mall.repository;

import com.hostflow.mall.entity.RetailTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RetailTenantRepository extends JpaRepository<RetailTenant, UUID> {
}
