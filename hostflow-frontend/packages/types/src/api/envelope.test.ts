import { describe, it, expect, expectTypeOf } from "vitest";
import type {
  ApiResponse,
  ApiSuccessResponse,
  ApiFailureResponse,
  PageResponse,
} from "./envelope";
import type { PropertyResponse } from "../property/property";

describe("ApiResponse discriminated union", () => {
  it("narrows to success shape when success is true", () => {
    const res: ApiResponse<PropertyResponse> = {
      success: true,
      data: {
        id: "1",
        name: "Nairobi Villa",
        propertyType: "VILLA",
        addressLine: "123 Karen Rd",
        city: "Nairobi",
        country: "KE",
        status: "PUBLISHED",
        createdAt: "2026-01-01T00:00:00Z",
        updatedAt: "2026-01-01T00:00:00Z",
      },
      timestamp: "2026-01-01T00:00:00Z",
    };
    if (res.success) {
      expectTypeOf(res).toEqualTypeOf<ApiSuccessResponse<PropertyResponse>>();
      expect(res.data.name).toBe("Nairobi Villa");
    }
  });

  it("narrows to failure shape when success is false", () => {
    const res: ApiResponse<PropertyResponse> = {
      success: false,
      error: { message: "Property not found", code: "NOT_FOUND" },
      timestamp: "2026-01-01T00:00:00Z",
    };
    if (!res.success) {
      expectTypeOf(res).toEqualTypeOf<ApiFailureResponse>();
      expect(res.error.message).toBe("Property not found");
    }
  });

  it("PageResponse holds a typed content array with pagination metadata", () => {
    const page: PageResponse<PropertyResponse> = {
      content: [],
      page: 0,
      size: 20,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: true,
    };
    expect(page.content).toEqual([]);
    expect(page.first).toBe(true);
  });
});
