// Mirrors module-booking's BookingResponse / CreateBookingRequest.

export type BookingStatus =
  "PENDING" | "CONFIRMED" | "CHECKED_IN" | "CHECKED_OUT" | "CANCELLED" | "DECLINED";

export interface BookingResponse {
  id: string;
  propertyId: string;
  guestUserId: string;
  checkIn: string;
  checkOut: string;
  totalPrice: string; // BigDecimal serialized as string — do not use number
  status: BookingStatus;
  /** Set only when status is DECLINED -- the owner's reason for not going ahead. */
  declineReason: string | null;
}

export interface CreateBookingRequest {
  propertyId: string;
  checkIn: string;
  checkOut: string;
  totalPrice: string;
}

// Mirrors ExternalCalendarLinkResponse -- the free iCal channel-sync feature.
export interface ExternalCalendarLinkResponse {
  id: string;
  propertyId: string;
  icsUrl: string;
  label: string | null;
  lastSyncedAt: string | null;
  lastSyncError: string | null;
}

export interface CreateExternalCalendarLinkRequest {
  propertyId: string;
  icsUrl: string;
  label?: string;
}
