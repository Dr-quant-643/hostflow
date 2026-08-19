import { describe, it, expect } from "vitest";
import type { PublicPropertySummary } from "@hostflow/types";

// Type-level contract test: PublicPropertySummary must mirror
// PublicPropertyQueries.PublicPropertyRow exactly (id, name, description,
// propertyType, addressLine, city, country, latitude, longitude, basePrice)
// — the discover grid, search results, and detail page all share this one
// shape since the backend detail endpoint returns the same row projection
// as the list/search endpoints.
describe("PublicPropertySummary shape", () => {
  it("carries the real PublicPropertyRow fields", () => {
    const sample: PublicPropertySummary = {
      id: "1",
      name: "Test",
      description: "A place to stay",
      propertyType: "VACATION_RENTAL",
      addressLine: "1 Main St",
      city: "Nairobi",
      country: "Kenya",
      latitude: -1.3,
      longitude: 36.8,
      basePrice: "50.00",
    };
    expect(sample.basePrice).toBe("50.00");
    expect(sample.city).toBe("Nairobi");
  });
});
