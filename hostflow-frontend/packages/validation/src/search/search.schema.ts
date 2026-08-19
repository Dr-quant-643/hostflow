import { z } from "zod";
import { isoDateSchema } from "../common";

export const propertySearchFormSchema = z
  .object({
    destination: z.string().optional(),
    checkIn: isoDateSchema,
    checkOut: isoDateSchema,
    guests: z.number().int().min(1).max(16),
  })
  .refine((data) => data.checkOut > data.checkIn, {
    message: "Check-out must be after check-in",
    path: ["checkOut"],
  });

export type PropertySearchFormValues = z.infer<typeof propertySearchFormSchema>;

// Deliberately separate from module-booking's bookingFormSchema (hostflow-web
// side, which takes propertyId + guestName as raw fields for an owner
// creating a booking on a guest's behalf). Real CreateBookingRequest is just
// {propertyId, checkIn, checkOut, totalPrice} — the guest's identity comes
// from their JWT server-side (GuestBookingController), not a form field, and
// there is no guest-count or special-requests column on the Booking entity,
// so this form only collects what the backend actually accepts. "guests" is
// carried through from the search step for display purposes only.
export const guestBookingFormSchema = z
  .object({
    checkIn: isoDateSchema,
    checkOut: isoDateSchema,
  })
  .refine((data) => data.checkOut > data.checkIn, {
    message: "Check-out must be after check-in",
    path: ["checkOut"],
  });

export type GuestBookingFormValues = z.infer<typeof guestBookingFormSchema>;
