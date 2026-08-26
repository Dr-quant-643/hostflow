package com.hostflow.booking.service;

import com.hostflow.booking.entity.ExternalCalendarLink;
import com.hostflow.booking.repository.ExternalCalendarLinkRepository;
import com.hostflow.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExternalCalendarLinkService {

    private final ExternalCalendarLinkRepository repository;

    public ExternalCalendarLinkService(ExternalCalendarLinkRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExternalCalendarLink create(UUID propertyId, String icsUrl, String label) {
        return repository.save(new ExternalCalendarLink(propertyId, icsUrl, label));
    }

    @Transactional(readOnly = true)
    public List<ExternalCalendarLink> listByProperty(UUID propertyId) {
        return repository.findByPropertyId(propertyId);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ExternalCalendarLink", id);
        }
        repository.deleteById(id);
    }
}
