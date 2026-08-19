import { z } from "zod";
import { uuidSchema, decimalStringSchema, isoDateSchema } from "../common";

export const createInvoiceSchema = z.object({
  bookingId: uuidSchema,
  billedUserId: uuidSchema,
  amount: decimalStringSchema,
  dueDate: isoDateSchema,
});

export type CreateInvoiceFormValues = z.infer<typeof createInvoiceSchema>;

// Mirrors the backend's ≤100-row sync limit (Backend Open Item H covers the
// >100 async path) — validating this client-side avoids a wasted round-trip
// for a batch the backend will reject outright.
export const batchCreateInvoicesSchema = z.object({
  invoices: z
    .array(createInvoiceSchema)
    .min(1, { message: "Add at least one invoice." })
    .max(100, {
      message: "Batch imports are limited to 100 invoices per request.",
    }),
});

export type BatchCreateInvoicesValues = z.infer<
  typeof batchCreateInvoicesSchema
>;
