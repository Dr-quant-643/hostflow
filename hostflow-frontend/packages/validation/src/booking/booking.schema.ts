import { z } from "zod";
import { uuidSchema, isoDateSchema, decimalStringSchema } from "../common";

export const createBookingSchema = z
  .object({
    propertyId: uuidSchema,
    checkIn: isoDateSchema,
    checkOut: isoDateSchema,
    totalPrice: decimalStringSchema,
  })
  .refine((data) => data.checkOut > data.checkIn, {
    message: "Check-out date must be after check-in date.",
    path: ["checkOut"],
  });

export type CreateBookingFormValues = z.infer<typeof createBookingSchema>;
