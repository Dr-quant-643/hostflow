package com.hostflow.property.messaging;

import com.hostflow.messaging.DomainEventMessage;
import com.hostflow.messaging.DomainEventPublisher;
import com.hostflow.messaging.RoutingKeys;
import com.hostflow.property.entity.Property;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PropertyEventPublisher {

    private final DomainEventPublisher publisher;

    public PropertyEventPublisher(DomainEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void created(Property property, UUID actorUserId) {
        publisher.publish(RoutingKeys.PROPERTY_CREATED, event(property, actorUserId, "PROPERTY_CREATED"));
    }

    public void updated(Property property, UUID actorUserId) {
        publisher.publish(RoutingKeys.PROPERTY_UPDATED, event(property, actorUserId, "PROPERTY_UPDATED"));
    }

    /** Fired on archive() — properties are never hard-deleted, archive is the
     * closest equivalent concept. */
    public void archived(Property property, UUID actorUserId) {
        publisher.publish(RoutingKeys.PROPERTY_DELETED, event(property, actorUserId, "PROPERTY_ARCHIVED"));
    }

    private DomainEventMessage event(Property property, UUID actorUserId, String action) {
        return new DomainEventMessage(property.getTenantId(), actorUserId, "Property", property.getId(), action, property.getName());
    }
}
