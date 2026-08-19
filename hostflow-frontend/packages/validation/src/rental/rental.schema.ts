import { z } from "zod";
import { uuidSchema, decimalStringSchema, isoDateSchema, nonEmptyString, emailSchema } from "../common";

export const rentalTenantFormSchema = z.object({
  fullName: nonEmptyString("Full name", 150),
  email: emailSchema.optional().or(z.literal("")),
  phone: z.string().max(30).optional(),
});

export type RentalTenantFormValues = z.infer<typeof rentalTenantFormSchema>;

export const leaseFormSchema = z
  .object({
    propertyId: uuidSchema,
    tenantIdRef: uuidSchema,
    startDate: isoDateSchema,
    endDate: isoDateSchema,
    monthlyRent: decimalStringSchema,
    securityDeposit: decimalStringSchema.optional().or(z.literal("")),
  })
  .refine((data) => data.endDate > data.startDate, {
    message: "End date must be after start date.",
    path: ["endDate"],
  });

export type LeaseFormValues = z.infer<typeof leaseFormSchema>;
