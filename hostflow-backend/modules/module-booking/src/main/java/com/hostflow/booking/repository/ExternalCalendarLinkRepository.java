package com.hostflow.booking.repository;

import com.hostflow.booking.entity.ExternalCalendarLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExternalCalendarLinkRepository extends JpaRepository<ExternalCalendarLink, UUID> {
    List<ExternalCalendarLink> findByPropertyId(UUID propertyId);
}
