package com.hostflow.booking.entity;

/**
 * Lifecycle: PENDING (created, awaiting confirmation/payment) -> CONFIRMED ->
 * CHECKED_IN -> CHECKED_OUT (terminal, successful), or PENDING/CONFIRMED -> CANCELLED
 * (terminal). No transition is allowed out of CHECKED_OUT or CANCELLED — enforced in
 * Booking.java's transition methods, not left to callers to get right.
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED,
    DECLINED
}
