package com.hostflow.crm.entity;

/**
 * LEAD -> QUALIFIED -> CUSTOMER is the "won" path. LOST is terminal, reachable
 * from LEAD or QUALIFIED. A CUSTOMER can never revert to LEAD/QUALIFIED/LOST —
 * enforced in Contact.java's transition methods, same discipline as
 * module-booking's BookingStatus transitions.
 */
public enum ContactStatus {
    LEAD,
    QUALIFIED,
    CUSTOMER,
    LOST
}
