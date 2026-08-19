package com.hostflow.office.repository;

import com.hostflow.office.entity.Visitor;
import com.hostflow.office.entity.VisitorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VisitorRepository extends JpaRepository<Visitor, UUID> {
    Page<Visitor> findByPropertyIdAndStatus(UUID propertyId, VisitorStatus status, Pageable pageable);
    Page<Visitor> findByPropertyId(UUID propertyId, Pageable pageable);
}
