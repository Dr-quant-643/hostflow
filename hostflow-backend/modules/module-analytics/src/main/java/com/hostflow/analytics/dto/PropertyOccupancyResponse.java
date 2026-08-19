package com.hostflow.analytics.dto;

import com.hostflow.analytics.entity.PropertyOccupancySummary;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertyOccupancyResponse(
        UUID propertyId, String propertyName, Long totalBookings, Long totalNightsBooked, BigDecimal totalRevenue
) {
    public static PropertyOccupancyResponse from(PropertyOccupancySummary summary) {
        return new PropertyOccupancyResponse(
                summary.getPropertyId(), summary.getPropertyName(), summary.getTotalBookings(),
                summary.getTotalNightsBooked(), summary.getTotalRevenue());
    }
}
