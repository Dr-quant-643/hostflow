import { z } from "zod";
import { uuidSchema, decimalStringSchema, isoDateSchema, nonEmptyString } from "../common";

export const expenseCategorySchema = z.enum([
  "MAINTENANCE",
  "UTILITIES",
  "STAFF",
  "MARKETING",
  "INSURANCE",
  "TAXES",
  "SUPPLIES",
  "OTHER",
]);

export const expenseFormSchema = z.object({
  propertyId: uuidSchema.optional(),
  category: expenseCategorySchema,
  description: nonEmptyString("Description", 500),
  amount: decimalStringSchema,
  expenseDate: isoDateSchema,
});

export type ExpenseFormValues = z.infer<typeof expenseFormSchema>;
