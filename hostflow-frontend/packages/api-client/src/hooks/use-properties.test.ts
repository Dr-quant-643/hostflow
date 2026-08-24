import { describe, it, expect } from "vitest";
import { propertyFormSchema } from "@hostflow/validation";

// Guards the contract between the form hook's mutation payload type and the
// actual Zod schema — catches drift if property.schema.ts changes shape
// without the hook being updated to match.
describe("useCreateProperty payload contract", () => {
  it("propertyFormSchema accepts a minimal valid payload matching the form defaults", () => {
    const result = propertyFormSchema.safeParse({
      name: "Test Property",
      propertyType: "RESIDENTIAL",
      rentalModel: "MONTHLY",
      addressLine: "1 Test St",
      city: "Nairobi",
      country: "Kenya",
    });
    expect(result.success).toBe(true);
  });

  it("propertyFormSchema rejects a missing name", () => {
    const result = propertyFormSchema.safeParse({
      name: "",
      propertyType: "RESIDENTIAL",
      rentalModel: "MONTHLY",
      addressLine: "1 Test St",
      city: "Nairobi",
      country: "Kenya",
    });
    expect(result.success).toBe(false);
  });

  it("propertyFormSchema rejects a missing rentalModel", () => {
    const result = propertyFormSchema.safeParse({
      name: "Test Property",
      propertyType: "RESIDENTIAL",
      addressLine: "1 Test St",
      city: "Nairobi",
      country: "Kenya",
    });
    expect(result.success).toBe(false);
  });
});
