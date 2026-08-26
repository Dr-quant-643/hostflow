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
