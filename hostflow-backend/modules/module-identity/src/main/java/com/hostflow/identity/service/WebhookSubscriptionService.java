package com.hostflow.identity.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.entity.WebhookSubscription;
import com.hostflow.identity.repository.WebhookSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class WebhookSubscriptionService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final WebhookSubscriptionRepository repository;

    public WebhookSubscriptionService(WebhookSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WebhookSubscription create(String url, String eventType) {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return repository.save(new WebhookSubscription(url, eventType, secret));
    }

    @Transactional(readOnly = true)
    public List<WebhookSubscription> list() {
        return repository.findAll();
    }

    @Transactional
    public void deactivate(UUID id) {
        WebhookSubscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WebhookSubscription", id));
        subscription.deactivate();
    }
}
