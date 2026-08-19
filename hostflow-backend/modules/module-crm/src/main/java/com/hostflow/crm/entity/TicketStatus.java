package com.hostflow.crm.entity;

/**
 * OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED is the normal path. RESOLVED can be
 * reopened back to IN_PROGRESS (guest/staff disagrees the issue is fixed) —
 * CLOSED
 * is terminal, matching how real support systems distinguish "resolved, pending
 * confirmation" from "actually done."
 */
public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
