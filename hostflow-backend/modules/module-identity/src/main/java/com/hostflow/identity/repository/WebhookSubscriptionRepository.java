package com.hostflow.identity.repository;

import com.hostflow.identity.entity.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, UUID> {
    List<WebhookSubscription> findByEventTypeAndActiveTrue(String eventType);
}
