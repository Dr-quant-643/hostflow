package com.hostflow.notification.repository;

import com.hostflow.notification.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
    Optional<DeviceToken> findByUserIdAndDeviceToken(UUID userId, String deviceToken);

    List<DeviceToken> findByUserIdAndActiveTrue(UUID userId);
}
