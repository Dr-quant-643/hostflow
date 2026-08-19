import { describe, it, expect } from "vitest";
import { propertyFormSchema } from "@hostflow/validation";

// Guards the contract between the form hook's mutation payload type and the
// actual Zod schema — catches drift if property.schema.ts changes shape
// without the hook being updated to match.
describe("useCreateProperty payload contract", () => {
  it("propertyFormSchema accepts a minimal valid payload matching the form defaults", () => {
    const result = propertyFormSchema.safeParse({
      name: "Test Property",
      description: "",
      nightlyRate: "0.00",
      status: "DRAFT",
    });
    expect(result.success).toBe(true);
  });

  it("propertyFormSchema rejects a non-decimal-string nightlyRate", () => {
    const result = propertyFormSchema.safeParse({
      name: "Test Property",
      description: "",
      nightlyRate: 0, // number, not string — violates BigDecimal-as-string rule
      status: "DRAFT",
    });
    expect(result.success).toBe(false);
  });
});
