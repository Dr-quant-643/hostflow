import { z } from "zod";
import { uuidSchema, decimalStringSchema, isoDateSchema } from "../common";
import { expenseCategorySchema } from "./expense.schema";

export const budgetFormSchema = z.object({
  propertyId: uuidSchema.optional(),
  category: expenseCategorySchema,
  budgetMonth: isoDateSchema,
  allocatedAmount: decimalStringSchema,
});

export type BudgetFormValues = z.infer<typeof budgetFormSchema>;
