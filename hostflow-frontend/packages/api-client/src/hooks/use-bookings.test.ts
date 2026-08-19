import { describe, it, expect } from "vitest";
import { bookingFormSchema } from "@hostflow/validation";

describe("useCreateBooking payload contract", () => {
  it("rejects checkOut before or equal to checkIn (cross-field refinement)", () => {
    const result = bookingFormSchema.safeParse({
      propertyId: "11111111-1111-1111-1111-111111111111",
      guestName: "Test Guest",
      checkIn: "2026-09-10",
      checkOut: "2026-09-09",
    });
    expect(result.success).toBe(false);
  });

  it("accepts a valid date range", () => {
    const result = bookingFormSchema.safeParse({
      propertyId: "11111111-1111-1111-1111-111111111111",
      guestName: "Test Guest",
      checkIn: "2026-09-10",
      checkOut: "2026-09-12",
    });
    expect(result.success).toBe(true);
  });
});
