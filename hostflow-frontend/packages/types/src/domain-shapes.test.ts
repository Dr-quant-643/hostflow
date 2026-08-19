import { describe, it, expect } from "vitest";
import type { BookingResponse, BookingStatus } from "./booking/booking";
import type { Campaign } from "./marketing/campaign";
import type { BatchCreateInvoicesResponse } from "./billing/invoice";

// These act as a contract-drift tripwire: if the backend renames or drops a
// field, a realistic fixture built against these types will fail to compile,
// which is the earliest possible signal (before any HTTP call is made).

describe("domain DTO shapes match backend contract", () => {
  it("BookingResponse covers full lifecycle status set", () => {
    const statuses: BookingStatus[] = [
      "PENDING",
      "CONFIRMED",
      "CHECKED_IN",
      "CHECKED_OUT",
      "CANCELLED",
    ];
    const sample: BookingResponse = {
      id: "b1",
      propertyId: "p1",
      checkIn: "2026-09-01",
      checkOut: "2026-09-05",
      totalPrice: "450.00",
      status: statuses[0],
      createdAt: "2026-08-01T00:00:00Z",
    };
    expect(statuses).toContain(sample.status);
  });

  it("Campaign is a plain content record with no AI generation state", () => {
    const sample: Campaign = {
      id: "c1",
      propertyId: "p1",
      name: "Autumn Promo",
      platform: "INSTAGRAM",
      status: "ARCHIVED",
      content: "Book your autumn getaway today.",
    };
    expect(sample.content).toBeDefined();
  });

  it("BatchCreateInvoicesResponse aggregates per-row results", () => {
    const sample: BatchCreateInvoicesResponse = {
      totalRequested: 2,
      succeeded: 1,
      failed: 1,
      results: [
        { index: 0, success: true, invoiceId: "inv1" },
        { index: 1, success: false, errorMessage: "Missing dueDate" },
      ],
    };
    expect(sample.results).toHaveLength(2);
    expect(sample.succeeded + sample.failed).toBe(sample.totalRequested);
  });
});
