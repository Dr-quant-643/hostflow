package com.hostflow.notification.service;

import com.hostflow.notification.entity.DeviceToken;
import com.hostflow.notification.repository.DeviceTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Closes the "no push device token storage" gap flagged in PROJECT_STATE.md —
 * PushDeliveryService.send() has always taken a deviceToken parameter with
 * nothing in the codebase to supply a real one. Wiring an actual push send
 * (looking up activeTokensFor() before calling NotificationService.send()) is
 * left to whichever future caller needs it — this only adds the missing
 * registration/storage layer itself.
 */
@Service
public class DeviceTokenService {

    private final DeviceTokenRepository repository;

    public DeviceTokenService(DeviceTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DeviceToken register(UUID userId, String deviceToken, String platform) {
        return repository.findByUserIdAndDeviceToken(userId, deviceToken)
                .map(existing -> {
                    if (!existing.isActive()) {
                        // re-registering a token that was previously unregistered
                        existing.reactivate();
                    }
                    return existing;
                })
                .orElseGet(() -> repository.save(new DeviceToken(userId, deviceToken, platform)));
    }

    @Transactional
    public void unregister(UUID userId, String deviceToken) {
        repository.findByUserIdAndDeviceToken(userId, deviceToken).ifPresent(DeviceToken::deactivate);
    }

    @Transactional(readOnly = true)
    public List<String> activeTokensFor(UUID userId) {
        return repository.findByUserIdAndActiveTrue(userId).stream().map(DeviceToken::getDeviceToken).toList();
    }
}
