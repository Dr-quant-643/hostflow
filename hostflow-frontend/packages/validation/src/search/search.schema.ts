import { z } from "zod";
import { isoDateSchema } from "../common";
import { rentalModelSchema } from "../property/property.schema";

// checkIn/checkOut are only meaningful for a NIGHTLY search -- a monthly
// rental has no "dates" to search by (see RentalModel's javadoc on the
// backend). Both stay optional at the object level so a MONTHLY search never
// has to fill in dates at all; superRefine enforces them only when searching
// NIGHTLY stays, same UX as before this field existed.
export const propertySearchFormSchema = z
  .object({
    destination: z.string().optional(),
    rentalModel: rentalModelSchema.default("NIGHTLY"),
    checkIn: isoDateSchema.optional().or(z.literal("")),
    checkOut: isoDateSchema.optional().or(z.literal("")),
    guests: z.number().int().min(1).max(16),
  })
  .superRefine((data, ctx) => {
    if (data.rentalModel !== "NIGHTLY") return;
    if (!data.checkIn) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: "Check-in is required", path: ["checkIn"] });
    }
    if (!data.checkOut) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: "Check-out is required", path: ["checkOut"] });
    }
    if (data.checkIn && data.checkOut && data.checkOut <= data.checkIn) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: "Check-out must be after check-in",
        path: ["checkOut"],
      });
    }
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
