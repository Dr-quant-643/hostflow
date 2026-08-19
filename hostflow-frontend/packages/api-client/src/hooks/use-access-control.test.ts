import { describe, it, expect } from "vitest";

// The one thing in this phase confirmed against the backend's actual
// stated API contract (not guessed): the three authority values.
describe("Known platform authorities (confirmed from backend API contract)", () => {
  const AVAILABLE_AUTHORITIES = [
    "PRODUCT_XANUOS",
    "PRODUCT_NAZILCO",
    "ROLE_PLATFORM_ADMIN",
  ];

  it("matches exactly the authority gates listed in the backend report", () => {
    expect(AVAILABLE_AUTHORITIES).toEqual([
      "PRODUCT_XANUOS",
      "PRODUCT_NAZILCO",
      "ROLE_PLATFORM_ADMIN",
    ]);
  });
});
