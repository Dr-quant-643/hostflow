package com.hostflow.property.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.property.dto.CreatePropertyRequest;
import com.hostflow.property.dto.UpdatePropertyDetailsRequest;
import com.hostflow.property.entity.Property;
import com.hostflow.property.messaging.PropertyEventPublisher;
import com.hostflow.property.repository.PropertyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyEventPublisher eventPublisher;

    public PropertyService(PropertyRepository propertyRepository, PropertyEventPublisher eventPublisher) {
        this.propertyRepository = propertyRepository;
        this.eventPublisher = eventPublisher;
    }

    /** RLS-scoped to the caller's own tenant, same as every other module's
     * plain findAll-backed list method (e.g. OrgUserAdminService). */
    @Transactional(readOnly = true)
    public List<Property> list(int limit, int offset) {
        return propertyRepository.findAll(PageRequest.of(offset / Math.max(limit, 1), limit)).getContent();
    }

    @Transactional
    public Property create(UUID ownerUserId, CreatePropertyRequest request) {
        Property property = new Property(
                ownerUserId, request.name(), request.propertyType(),
                request.addressLine(), request.city(), request.country()
        );
        property = propertyRepository.save(property);
        eventPublisher.created(property, ownerUserId);
        return property;
    }

    @Transactional(readOnly = true)
    public Property getById(UUID propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
    }

    @Transactional
    public Property updateDetails(UUID propertyId, UUID actorUserId, UpdatePropertyDetailsRequest request) {
        Property property = getById(propertyId);
        if (request.description() != null) {
            property.updateDescription(request.description());
        }
        if (request.basePrice() != null) {
            property.updateBasePrice(request.basePrice());
        }
        if (request.latitude() != null && request.longitude() != null) {
            property.updateLocation(request.latitude(), request.longitude());
        }
        eventPublisher.updated(property, actorUserId);
        return property;
    }

    @Transactional
    public Property publish(UUID propertyId, UUID actorUserId) {
        Property property = getById(propertyId);
        property.publish();
        eventPublisher.updated(property, actorUserId);
        return property;
    }

    @Transactional
    public Property archive(UUID propertyId, UUID actorUserId) {
        Property property = getById(propertyId);
        property.archive();
        eventPublisher.archived(property, actorUserId);
        return property;
    }
}
