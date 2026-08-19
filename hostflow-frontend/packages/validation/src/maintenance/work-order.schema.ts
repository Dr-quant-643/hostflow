import { z } from "zod";
import { uuidSchema, nonEmptyString } from "../common";

export const maintenanceCategorySchema = z.enum([
  "PLUMBING",
  "ELECTRICAL",
  "HVAC",
  "APPLIANCE",
  "STRUCTURAL",
  "PEST_CONTROL",
  "CLEANING",
  "OTHER",
]);

export const workOrderFormSchema = z.object({
  propertyId: uuidSchema,
  category: maintenanceCategorySchema,
  title: nonEmptyString("Title", 200),
  description: z.string().max(2000).optional(),
  priority: z.enum(["LOW", "MEDIUM", "HIGH", "EMERGENCY"]),
});

export type WorkOrderFormValues = z.infer<typeof workOrderFormSchema>;
