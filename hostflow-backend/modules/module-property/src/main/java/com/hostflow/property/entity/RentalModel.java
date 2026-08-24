package com.hostflow.property.entity;

/**
 * Set explicitly by the owner at creation time, never derived purely from
 * PropertyType -- OFFICE and RETAIL_MALL listings are genuinely ambiguous
 * (a hall might be a day-rate conference venue or a monthly-rent workspace
 * lease; only the owner listing it knows which). NIGHTLY keeps the existing
 * check-in/check-out Booking flow; MONTHLY properties skip that flow
 * entirely in favor of module-rental's Lease-based system (a landlord vets
 * the tenant and creates the lease directly, same as real-world leasing --
 * not instant self-service booking).
 */
public enum RentalModel {
    NIGHTLY,
    MONTHLY
}
