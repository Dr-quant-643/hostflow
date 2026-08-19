import { z } from "zod";
import { uuidSchema, decimalStringSchema } from "../common";

export const recordPaymentSchema = z.object({
  invoiceId: uuidSchema,
  amount: decimalStringSchema,
  providerReference: z.string().max(200).optional(),
});

export type RecordPaymentFormValues = z.infer<typeof recordPaymentSchema>;
