package com.hostflow.identity.messaging;

import com.hostflow.identity.entity.Organization;
import com.hostflow.messaging.DomainEventMessage;
import com.hostflow.messaging.DomainEventPublisher;
import com.hostflow.messaging.RoutingKeys;
import org.springframework.stereotype.Component;

@Component
public class TenantEventPublisher {

    private final DomainEventPublisher publisher;

    public TenantEventPublisher(DomainEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void created(Organization organization) {
        publisher.publish(RoutingKeys.TENANT_CREATED,
                new DomainEventMessage(organization.getId(), null, "Organization", organization.getId(),
                        "TENANT_CREATED", organization.getName()));
    }

    public void updated(Organization organization) {
        publisher.publish(RoutingKeys.TENANT_UPDATED,
                new DomainEventMessage(organization.getId(), null, "Organization", organization.getId(),
                        "TENANT_UPDATED", organization.getName()));
    }
}
