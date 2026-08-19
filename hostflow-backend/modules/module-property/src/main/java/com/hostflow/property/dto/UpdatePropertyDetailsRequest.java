package com.hostflow.property.dto;

import java.math.BigDecimal;

/**
 * Property.updateDescription()/updateBasePrice()/updateLocation() existed on
 * the entity with no controller/service path ever calling them — a property
 * could be created, published, and archived, but never priced or placed on a
 * map. This bundles the three into one partial-update request rather than
 * three separate endpoints, since they're set together in practice (a host
 * finishing their listing before publishing it).
 */
public record UpdatePropertyDetailsRequest(
        String description,
        BigDecimal basePrice,
        Double latitude,
        Double longitude
) {
}
