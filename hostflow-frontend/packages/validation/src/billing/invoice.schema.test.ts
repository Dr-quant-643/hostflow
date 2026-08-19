import { describe, it, expect } from "vitest";
import {
  batchCreateInvoicesSchema,
  createInvoiceSchema,
} from "./invoice.schema";

const validInvoice = {
  bookingId: "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  billedUserId: "3fa85f64-5717-4562-b3fc-2c963f66afa7",
  amount: "1200.00",
  dueDate: "2026-09-30",
};

describe("createInvoiceSchema", () => {
  it("accepts a valid invoice", () => {
    expect(createInvoiceSchema.safeParse(validInvoice).success).toBe(true);
  });
});

describe("batchCreateInvoicesSchema", () => {
  it("rejects an empty batch", () => {
    expect(batchCreateInvoicesSchema.safeParse({ invoices: [] }).success).toBe(
      false,
    );
  });

  it("rejects a batch over 100 rows, mirroring the backend sync limit", () => {
    const invoices = Array.from({ length: 101 }, () => validInvoice);
    const result = batchCreateInvoicesSchema.safeParse({ invoices });
    expect(result.success).toBe(false);
  });

  it("accepts a batch of exactly 100 rows", () => {
    const invoices = Array.from({ length: 100 }, () => validInvoice);
    expect(batchCreateInvoicesSchema.safeParse({ invoices }).success).toBe(
      true,
    );
  });
});
