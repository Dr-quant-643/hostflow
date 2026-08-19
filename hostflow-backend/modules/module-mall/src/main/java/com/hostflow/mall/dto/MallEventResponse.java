package com.hostflow.mall.dto;

import com.hostflow.mall.entity.MallEvent;

import java.time.Instant;
import java.util.UUID;

public record MallEventResponse(UUID id, String title, String description, Instant startsAt, Instant endsAt) {
    public static MallEventResponse from(MallEvent e) {
        return new MallEventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getStartsAt(), e.getEndsAt());
    }
}
