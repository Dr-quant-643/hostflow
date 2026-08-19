import { z } from "zod";
import { uuidSchema, decimalStringSchema, nonEmptyString, emailSchema } from "../common";

export const retailUnitFormSchema = z.object({
  propertyId: uuidSchema,
  unitNumber: nonEmptyString("Unit number", 50),
  sizeSqm: decimalStringSchema.optional().or(z.literal("")),
});

export type RetailUnitFormValues = z.infer<typeof retailUnitFormSchema>;

export const assignRetailTenantFormSchema = z.object({
  retailUnitId: uuidSchema,
  businessName: nonEmptyString("Business name", 200),
  contactEmail: emailSchema.optional().or(z.literal("")),
  contactPhone: z.string().max(30).optional(),
  monthlyRent: decimalStringSchema,
  revenueSharePercent: decimalStringSchema.optional().or(z.literal("")),
});

export type AssignRetailTenantFormValues = z.infer<typeof assignRetailTenantFormSchema>;

export const mallEventFormSchema = z
  .object({
    propertyId: uuidSchema,
    title: nonEmptyString("Title", 200),
    description: z.string().max(2000).optional(),
    startsAt: nonEmptyString("Start time"),
    endsAt: nonEmptyString("End time"),
  })
  .refine((data) => data.endsAt > data.startsAt, {
    message: "End time must be after start time.",
    path: ["endsAt"],
  });

export type MallEventFormValues = z.infer<typeof mallEventFormSchema>;

export const parkingEntryFormSchema = z.object({
  propertyId: uuidSchema,
  vehiclePlate: nonEmptyString("Vehicle plate", 20),
});

export type ParkingEntryFormValues = z.infer<typeof parkingEntryFormSchema>;
