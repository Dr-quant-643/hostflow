package com.hostflow.notification.delivery;

import com.hostflow.common.exception.ApplicationException;
import com.hostflow.common.exception.ErrorCode;

/**
 * Thrown by any DeliveryService when the underlying provider call fails. Caught
 * by NotificationConsumer, which marks the NotificationLog FAILED and rethrows —
 * the rethrow is what drives Spring AMQP's retry/DLQ mechanism, exactly the same
 * pattern the original (simulated) consumer used, now backed by real failures
 * instead of never failing at all.
 */
public class DeliveryException extends ApplicationException {

    public DeliveryException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, message, cause);
    }
}
