import { describe, it, expect } from "vitest";
import { propertySearchFormSchema } from "./search.schema";

describe("propertySearchFormSchema", () => {
  it("requires checkIn/checkOut for a NIGHTLY search", () => {
    const result = propertySearchFormSchema.safeParse({
      rentalModel: "NIGHTLY",
      guests: 2,
    });
    expect(result.success).toBe(false);
  });

  it("rejects checkOut before/equal to checkIn for a NIGHTLY search", () => {
    const result = propertySearchFormSchema.safeParse({
      rentalModel: "NIGHTLY",
      checkIn: "2026-09-12",
      checkOut: "2026-09-10",
      guests: 2,
    });
    expect(result.success).toBe(false);
  });

  it("accepts a valid NIGHTLY search", () => {
    const result = propertySearchFormSchema.safeParse({
      rentalModel: "NIGHTLY",
      checkIn: "2026-09-10",
      checkOut: "2026-09-12",
      guests: 2,
    });
    expect(result.success).toBe(true);
  });

  it("does not require checkIn/checkOut for a MONTHLY search", () => {
    const result = propertySearchFormSchema.safeParse({
      rentalModel: "MONTHLY",
      guests: 1,
    });
    expect(result.success).toBe(true);
  });

  it("defaults rentalModel to NIGHTLY when omitted", () => {
    const result = propertySearchFormSchema.safeParse({
      checkIn: "2026-09-10",
      checkOut: "2026-09-12",
      guests: 1,
    });
    expect(result.success).toBe(true);
    if (result.success) {
      expect(result.data.rentalModel).toBe("NIGHTLY");
    }
  });
});
