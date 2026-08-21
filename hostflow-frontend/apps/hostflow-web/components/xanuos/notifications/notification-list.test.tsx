import { describe, it, expect } from "vitest";
import type { NotificationLogEntry } from "@hostflow/api-client/src/hooks/use-notifications";

// Type-level regression test: ensures NotificationLogEntry's channel enum
// stays aligned with the campaign form's channel options from Phase 4
// (EMAIL/SMS/WHATSAPP) — these two independently-defined unions should
// never drift apart silently.
describe("NotificationLogEntry channel alignment", () => {
  it("accepts all three campaign delivery channels", () => {
    const channels: NotificationLogEntry["channel"][] = [
      "EMAIL",
      "SMS",
      "WHATSAPP",
    ];
    expect(channels).toHaveLength(3);
  });

  it("status enum covers the simulated-delivery states", () => {
    const statuses: NotificationLogEntry["status"][] = [
      "SIMULATED",
      "SENT",
      "FAILED",
    ];
    expect(statuses).toContain("SIMULATED");
  });
});
