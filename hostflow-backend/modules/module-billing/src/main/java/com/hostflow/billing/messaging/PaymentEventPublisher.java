package com.hostflow.billing.messaging;

import com.hostflow.billing.entity.Payment;
import com.hostflow.messaging.DomainEventMessage;
import com.hostflow.messaging.DomainEventPublisher;
import com.hostflow.messaging.RoutingKeys;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final DomainEventPublisher publisher;

    public PaymentEventPublisher(DomainEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void succeeded(Payment payment) {
        publisher.publish(RoutingKeys.PAYMENT_SUCCESS, event(payment, "PAYMENT_SUCCEEDED"));
    }

    public void failed(Payment payment) {
        publisher.publish(RoutingKeys.PAYMENT_FAILED, event(payment, "PAYMENT_FAILED"));
    }

    public void refunded(Payment payment) {
        publisher.publish(RoutingKeys.PAYMENT_REFUNDED, event(payment, "PAYMENT_REFUNDED"));
    }

    private DomainEventMessage event(Payment payment, String action) {
        return new DomainEventMessage(payment.getTenantId(), null, "Payment", payment.getId(), action,
                "invoice=" + payment.getInvoiceId() + " amount=" + payment.getAmount());
    }
}
