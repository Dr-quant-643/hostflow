package com.hostflow.office.dto;

import com.hostflow.office.entity.MeetingRoom;

import java.util.UUID;

public record MeetingRoomResponse(UUID id, UUID propertyId, String name, Integer capacity, boolean active) {
    public static MeetingRoomResponse from(MeetingRoom room) {
        return new MeetingRoomResponse(room.getId(), room.getPropertyId(), room.getName(), room.getCapacity(), room.isActive());
    }
}
