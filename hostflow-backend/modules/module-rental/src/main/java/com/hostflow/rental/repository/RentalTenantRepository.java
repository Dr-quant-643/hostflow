package com.hostflow.rental.repository;

import com.hostflow.rental.entity.RentalTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalTenantRepository extends JpaRepository<RentalTenant, UUID> {
}
