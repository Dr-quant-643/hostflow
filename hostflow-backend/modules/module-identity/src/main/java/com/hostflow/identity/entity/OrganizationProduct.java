package com.hostflow.identity.entity;

/**
 * Which HostFlow product this organization primarily onboarded through. Mirrors
 * core-security's ProductScope (XANUOS/NAZILCO/PLATFORM) but is intentionally a
 * SEPARATE enum: this describes what the ORGANIZATION is, ProductScope describes
 * what a given USER'S TOKEN can access. A XanuOS organization's staff member could
 * still be granted NazilCo product_scope for support purposes without that changing
 * what kind of organization they belong to.
 */
public enum OrganizationProduct {
    XANUOS,
    NAZILCO
}
