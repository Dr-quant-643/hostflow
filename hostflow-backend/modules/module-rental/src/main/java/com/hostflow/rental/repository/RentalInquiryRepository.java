package com.hostflow.rental.repository;

import com.hostflow.rental.entity.RentalInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalInquiryRepository extends JpaRepository<RentalInquiry, UUID> {
    Page<RentalInquiry> findByPropertyId(UUID propertyId, Pageable pageable);
}
