package com.hostflow.crm.repository;

import com.hostflow.crm.entity.Contact;
import com.hostflow.crm.entity.ContactStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByStatus(ContactStatus status, Pageable pageable);
    Optional<Contact> findByEmail(String email);
}
