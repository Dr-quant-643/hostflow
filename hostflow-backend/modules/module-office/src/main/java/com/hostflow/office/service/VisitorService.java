package com.hostflow.office.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.office.dto.RegisterVisitorRequest;
import com.hostflow.office.entity.Visitor;
import com.hostflow.office.entity.VisitorStatus;
import com.hostflow.office.repository.VisitorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VisitorService {

    private final VisitorRepository repository;

    public VisitorService(VisitorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Visitor register(UUID hostedByUserId, RegisterVisitorRequest request) {
        Visitor visitor = new Visitor(request.propertyId(), hostedByUserId, request.fullName(), request.company(), request.expectedAt());
        return repository.save(visitor);
    }

    @Transactional
    public Visitor checkIn(UUID id) {
        Visitor visitor = getById(id);
        visitor.checkIn();
        return visitor;
    }

    @Transactional
    public Visitor checkOut(UUID id) {
        Visitor visitor = getById(id);
        visitor.checkOut();
        return visitor;
    }

    @Transactional(readOnly = true)
    public Page<Visitor> listByProperty(UUID propertyId, int limit, int offset) {
        return repository.findByPropertyId(propertyId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional(readOnly = true)
    public long countCheckedIn() {
        return repository.countByStatus(VisitorStatus.CHECKED_IN);
    }

    private Visitor getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Visitor", id));
    }
}
