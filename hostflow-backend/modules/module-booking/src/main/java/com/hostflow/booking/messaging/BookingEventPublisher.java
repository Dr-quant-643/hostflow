package com.hostflow.booking.messaging;

import com.hostflow.booking.entity.Booking;
import com.hostflow.messaging.DomainEventMessage;
import com.hostflow.messaging.DomainEventPublisher;
import com.hostflow.messaging.RoutingKeys;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookingEventPublisher {

    private final DomainEventPublisher publisher;

    public BookingEventPublisher(DomainEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void created(Booking booking, UUID actorUserId) {
        publisher.publish(RoutingKeys.BOOKING_CREATED, event(booking, actorUserId, "BOOKING_CREATED"));
    }

    public void confirmed(Booking booking, UUID actorUserId) {
        publisher.publish(RoutingKeys.BOOKING_CONFIRMED, event(booking, actorUserId, "BOOKING_CONFIRMED"));
    }

    public void cancelled(Booking booking, UUID actorUserId) {
        publisher.publish(RoutingKeys.BOOKING_CANCELLED, event(booking, actorUserId, "BOOKING_CANCELLED"));
    }

    /**
     * Reuses the BOOKING_CANCELLED routing key/queue rather than adding a new
     * one -- the only current consumer (DomainAuditEventConsumer.bookingCancelled)
     * just writes an audit row keyed off event.action(), which "BOOKING_DECLINED"
     * satisfies exactly as well as a dedicated queue would, with no new topology.
     */
    public void declined(Booking booking, UUID actorUserId, String reason) {
        publisher.publish(RoutingKeys.BOOKING_CANCELLED,
                new DomainEventMessage(booking.getTenantId(), actorUserId, "Booking", booking.getId(),
                        "BOOKING_DECLINED",
                        "property=" + booking.getPropertyId() + " reason=" + reason));
    }

    public void expired(Booking booking) {
        publisher.publish(RoutingKeys.BOOKING_EXPIRED, event(booking, null, "BOOKING_EXPIRED"));
    }

    private DomainEventMessage event(Booking booking, UUID actorUserId, String action) {
        return new DomainEventMessage(booking.getTenantId(), actorUserId, "Booking", booking.getId(), action,
                "property=" + booking.getPropertyId() + " checkIn=" + booking.getCheckIn() + " checkOut=" + booking.getCheckOut());
    }
}
