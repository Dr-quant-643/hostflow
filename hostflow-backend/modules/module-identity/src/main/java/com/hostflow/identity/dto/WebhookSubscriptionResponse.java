package com.hostflow.identity.dto;

import com.hostflow.identity.entity.WebhookSubscription;

import java.util.UUID;

/** Unlike ApiKeyResponse, this DOES carry the secret on every read -- a
 *  webhook secret is used by the OWNER's own receiving server to verify
 *  incoming payloads, so (unlike a bearer credential) they need to be able
 *  to retrieve it again, not just see it once. */
public record WebhookSubscriptionResponse(UUID id, String url, String eventType, String secret, boolean active) {
    public static WebhookSubscriptionResponse from(WebhookSubscription subscription) {
        return new WebhookSubscriptionResponse(subscription.getId(), subscription.getUrl(), subscription.getEventType(),
                subscription.getSecret(), subscription.isActive());
    }
}
