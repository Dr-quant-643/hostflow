package com.hostflow.booking.dto;

import com.hostflow.booking.entity.ExternalCalendarLink;

import java.time.Instant;
import java.util.UUID;

public record ExternalCalendarLinkResponse(
        UUID id, UUID propertyId, String icsUrl, String label, Instant lastSyncedAt, String lastSyncError
) {
    public static ExternalCalendarLinkResponse from(ExternalCalendarLink link) {
        return new ExternalCalendarLinkResponse(link.getId(), link.getPropertyId(), link.getIcsUrl(), link.getLabel(),
                link.getLastSyncedAt(), link.getLastSyncError());
    }
}
