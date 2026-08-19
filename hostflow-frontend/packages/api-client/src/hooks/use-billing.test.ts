import { describe, it, expect } from "vitest";
import { batchCreateInvoicesSchema } from "@hostflow/validation";

describe("batchCreateInvoicesSchema row cap (mirrors backend Open Item H)", () => {
  const makeInvoice = (n: number) => ({
    guestName: `Guest ${n}`,
    amount: "100.00",
    dueDate: "2026-09-01",
  });

  it("accepts exactly 100 rows", () => {
    const invoices = Array.from({ length: 100 }, (_, i) => makeInvoice(i));
    const result = batchCreateInvoicesSchema.safeParse({ invoices });
    expect(result.success).toBe(true);
  });

  it("rejects 101 rows — client-side mirror of the sync-only backend limit", () => {
    const invoices = Array.from({ length: 101 }, (_, i) => makeInvoice(i));
    const result = batchCreateInvoicesSchema.safeParse({ invoices });
    expect(result.success).toBe(false);
  });
});
