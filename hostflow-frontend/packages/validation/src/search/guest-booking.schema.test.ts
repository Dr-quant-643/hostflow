import { describe, it, expect } from "vitest";
import { guestBookingFormSchema } from "./search.schema";

describe("guestBookingFormSchema", () => {
  it("accepts a valid guest booking", () => {
    const result = guestBookingFormSchema.safeParse({
      checkIn: "2026-09-10",
      checkOut: "2026-09-12",
    });
    expect(result.success).toBe(true);
  });

  it("rejects checkOut before checkIn, same as the owner-side booking schema", () => {
    const result = guestBookingFormSchema.safeParse({
      checkIn: "2026-09-12",
      checkOut: "2026-09-10",
    });
    expect(result.success).toBe(false);
  });

  it("rejects checkOut equal to checkIn", () => {
    const result = guestBookingFormSchema.safeParse({
      checkIn: "2026-09-10",
      checkOut: "2026-09-10",
    });
    expect(result.success).toBe(false);
  });
});
