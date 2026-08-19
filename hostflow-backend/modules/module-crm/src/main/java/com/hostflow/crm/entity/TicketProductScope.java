package com.hostflow.crm.entity;

/**
 * Separate from core-security's ProductScope on purpose — same reasoning as
 * module-identity's OrganizationProduct: this describes what the TICKET is
 * about
 * (a XanuOS support issue vs. a NazilCo one), not what a token can access. Both
 * hostflow-admin and nazilco-admin query this same table, filtered by this
 * field.
 */
public enum TicketProductScope {
    XANUOS,
    NAZILCO
}
