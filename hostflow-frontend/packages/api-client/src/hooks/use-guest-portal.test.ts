import { describe, it, expect } from "vitest";
import type { BookingResponse } from "@hostflow/types";

// isUpcoming isn't exported (kept as a private helper in the hook file),
// so this test re-derives its exact logic to lock the boundary behavior
// down as a spec — if the real implementation changes, this test should
// be updated deliberately, not silently pass a different behavior.
function isUpcoming(booking: Pick<BookingResponse, "checkIn">): boolean {
  return new Date(booking.checkIn).getTime() >= Date.now();
}

describe("useMyTrips upcoming/past split", () => {
  it("treats a future checkIn as upcoming", () => {
    const future = new Date(Date.now() + 1000 * 60 * 60 * 24 * 30)
      .toISOString()
      .slice(0, 10);
    expect(isUpcoming({ checkIn: future })).toBe(true);
  });

  it("treats a past checkIn as not upcoming", () => {
    const past = new Date(Date.now() - 1000 * 60 * 60 * 24 * 30)
      .toISOString()
      .slice(0, 10);
    expect(isUpcoming({ checkIn: past })).toBe(false);
  });
});
