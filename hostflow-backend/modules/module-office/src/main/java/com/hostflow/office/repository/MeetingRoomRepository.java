package com.hostflow.office.repository;

import com.hostflow.office.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, UUID> {
    List<MeetingRoom> findByPropertyIdAndActiveTrue(UUID propertyId);
}
