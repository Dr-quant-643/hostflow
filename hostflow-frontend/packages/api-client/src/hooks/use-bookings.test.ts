import { describe, it, expect } from "vitest";
import { createBookingSchema } from "@hostflow/validation";

describe("useCreateBooking payload contract", () => {
  it("rejects checkOut before or equal to checkIn (cross-field refinement)", () => {
    const result = createBookingSchema.safeParse({
      propertyId: "11111111-1111-1111-1111-111111111111",
      checkIn: "2026-09-10",
      checkOut: "2026-09-09",
      totalPrice: "100.00",
    });
    expect(result.success).toBe(false);
  });

  it("accepts a valid date range", () => {
    const result = createBookingSchema.safeParse({
      propertyId: "11111111-1111-1111-1111-111111111111",
      checkIn: "2026-09-10",
      checkOut: "2026-09-12",
      totalPrice: "100.00",
    });
    expect(result.success).toBe(true);
  });
});
