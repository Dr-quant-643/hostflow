import { describe, it, expect } from "vitest";
import { createBookingSchema } from "./booking.schema";

const validBooking = {
  propertyId: "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  checkIn: "2026-09-01",
  checkOut: "2026-09-05",
  totalPrice: "450.00",
};

describe("createBookingSchema", () => {
  it("accepts a valid booking", () => {
    expect(createBookingSchema.safeParse(validBooking).success).toBe(true);
  });

  it("rejects checkOut before checkIn", () => {
    const result = createBookingSchema.safeParse({
      ...validBooking,
      checkIn: "2026-09-10",
      checkOut: "2026-09-05",
    });
    expect(result.success).toBe(false);
    if (!result.success) {
      expect(result.error.issues[0].path).toEqual(["checkOut"]);
    }
  });

  it("rejects an invalid UUID propertyId", () => {
    const result = createBookingSchema.safeParse({
      ...validBooking,
      propertyId: "not-a-uuid",
    });
    expect(result.success).toBe(false);
  });

  it("rejects a malformed date", () => {
    const result = createBookingSchema.safeParse({
      ...validBooking,
      checkIn: "09/01/2026",
    });
    expect(result.success).toBe(false);
  });

  it("rejects a totalPrice with more than 2 decimal places", () => {
    const result = createBookingSchema.safeParse({
      ...validBooking,
      totalPrice: "450.999",
    });
    expect(result.success).toBe(false);
  });
});
