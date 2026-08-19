import { z } from "zod";
import { uuidSchema, nonEmptyString, isoDateSchema } from "../common";
import { maintenanceCategorySchema } from "./work-order.schema";

export const assetFormSchema = z.object({
  propertyId: uuidSchema,
  name: nonEmptyString("Name", 150),
  category: z.string().max(100).optional(),
  serialNumber: z.string().max(100).optional(),
  purchaseDate: isoDateSchema.optional().or(z.literal("")),
  warrantyExpiryDate: isoDateSchema.optional().or(z.literal("")),
});

export type AssetFormValues = z.infer<typeof assetFormSchema>;

export const maintenanceScheduleFormSchema = z.object({
  propertyId: uuidSchema,
  assetId: uuidSchema.optional().or(z.literal("")),
  category: maintenanceCategorySchema,
  title: nonEmptyString("Title", 200),
  intervalDays: z.coerce.number().int().positive(),
  firstDueDate: isoDateSchema,
});

export type MaintenanceScheduleFormValues = z.infer<typeof maintenanceScheduleFormSchema>;
