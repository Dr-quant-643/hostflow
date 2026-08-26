import type { BookingStatus, LeaseStatus, GuestSegment } from "@hostflow/types";

// Shared color mapping for Booking/Lease status badges -- PENDING/DRAFT read
// as "needs your attention" (amber), CONFIRMED/ACTIVE as "good to go"
// (green), DECLINED/CANCELLED as a clear stop (red), everything else neutral.
export const BOOKING_STATUS_VARIANT: Record<BookingStatus, "success" | "warning" | "destructive" | "outline"> = {
  PENDING: "warning",
  CONFIRMED: "success",
  CHECKED_IN: "success",
  CHECKED_OUT: "outline",
  CANCELLED: "destructive",
  DECLINED: "destructive",
};

export const LEASE_STATUS_VARIANT: Record<LeaseStatus, "success" | "warning" | "destructive" | "outline"> = {
  DRAFT: "warning",
  ACTIVE: "success",
  EXPIRED: "outline",
  TERMINATED: "destructive",
  DECLINED: "destructive",
};

// Guest/tenant segment badges -- VIP gets the brand color (stands out on
// purpose), AT_RISK reuses the same warning tone as a pending approval (both
// mean "needs your attention"), REPEAT/ACTIVE_TENANT read as healthy.
export const GUEST_SEGMENT_VARIANT: Record<GuestSegment, "default" | "success" | "warning" | "outline" | "secondary"> = {
  ACTIVE_TENANT: "success",
  VIP: "default",
  REPEAT: "secondary",
  AT_RISK: "warning",
  NEW: "outline",
};

export const GUEST_SEGMENT_LABEL: Record<GuestSegment, string> = {
  ACTIVE_TENANT: "Active tenant",
  VIP: "VIP",
  REPEAT: "Repeat",
  AT_RISK: "At risk",
  NEW: "New",
};
