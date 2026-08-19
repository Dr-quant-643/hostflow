package com.hostflow.messaging;

import com.hostflow.messaging.config.HostFlowRabbitProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Thin wrapper so every module's event publisher (BookingEventPublisher,
 * PropertyEventPublisher, etc.) doesn't repeat exchange-lookup boilerplate. */
@Component
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final HostFlowRabbitProperties properties;

    public DomainEventPublisher(RabbitTemplate rabbitTemplate, HostFlowRabbitProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public void publish(String routingKey, DomainEventMessage event) {
        rabbitTemplate.convertAndSend(properties.getExchanges().getDirect(), routingKey, event);
    }
}
