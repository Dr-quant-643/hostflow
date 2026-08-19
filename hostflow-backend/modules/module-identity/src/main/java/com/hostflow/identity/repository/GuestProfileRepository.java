package com.hostflow.identity.repository;

import com.hostflow.identity.entity.GuestProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * No RLS involved (GuestProfile is not tenant-owned data) — a plain
 * JpaRepository
 * against the hostflow_app connection is correct here, same as
 * OrganizationRepository.
 */
public interface GuestProfileRepository extends JpaRepository<GuestProfile, UUID> {
    boolean existsByEmail(String email);

    Optional<GuestProfile> findByKeycloakId(String keycloakId);
}
