package com.hostflow.mall.repository;

import com.hostflow.mall.entity.MallEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MallEventRepository extends JpaRepository<MallEvent, UUID> {
    List<MallEvent> findByPropertyId(UUID propertyId);
}
