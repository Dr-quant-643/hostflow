package com.hostflow.crm.repository;

import com.hostflow.crm.entity.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {
    List<Interaction> findByContactIdOrderByCreatedAtDesc(UUID contactId);
}
