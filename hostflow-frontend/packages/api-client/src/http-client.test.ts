import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { apiFetch, api } from "./http-client";
import { ApiError } from "./errors";

function mockFetchOnce(body: unknown, status = 200) {
  return vi.fn().mockResolvedValueOnce({
    status,
    json: async () => body,
  } as Response);
}

describe("apiFetch", () => {
  beforeEach(() => {
    vi.stubGlobal("process", {
      env: {
        NODE_ENV: "test",
        NEXT_PUBLIC_API_BASE_URL: "http://localhost:8085/api/v1",
      },
    });
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  it("unwraps a successful envelope and returns data", async () => {
    vi.stubGlobal(
      "fetch",
      mockFetchOnce({
        success: true,
        data: { id: "1", name: "Villa" },
        timestamp: "now",
      }),
    );
    const result = await apiFetch<{ id: string; name: string }>(
      "/properties/1",
    );
    expect(result).toEqual({ id: "1", name: "Villa" });
  });

  it("throws ApiError on a success:false envelope", async () => {
    vi.stubGlobal(
      "fetch",
      mockFetchOnce(
        {
          success: false,
          error: { message: "Property not found", code: "NOT_FOUND" },
          timestamp: "now",
        },
        404,
      ),
    );
    await expect(apiFetch("/properties/missing")).rejects.toMatchObject({
      message: "Property not found",
      code: "NOT_FOUND",
      status: 404,
    });
  });

  it("throws ApiError.network after exhausting retries on network failure", async () => {
    const fetchMock = vi
      .fn()
      .mockRejectedValue(new TypeError("Failed to fetch"));
    vi.stubGlobal("fetch", fetchMock);
    await expect(
      apiFetch("/properties/1", { retries: 1 }),
    ).rejects.toMatchObject({
      code: "NETWORK_ERROR",
    });
    expect(fetchMock).toHaveBeenCalledTimes(2); // initial + 1 retry
  });

  it("does not retry POST requests on network failure", async () => {
    const fetchMock = vi
      .fn()
      .mockRejectedValue(new TypeError("Failed to fetch"));
    vi.stubGlobal("fetch", fetchMock);
    await expect(
      api.post("/properties", { name: "New" }),
    ).rejects.toBeInstanceOf(ApiError);
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("sends X-XSRF-TOKEN header on mutating requests when cookie present", async () => {
    Object.defineProperty(document, "cookie", {
      value: "XSRF-TOKEN=abc123",
      writable: true,
    });
    const fetchMock = mockFetchOnce({
      success: true,
      data: {},
      timestamp: "now",
    });
    vi.stubGlobal("fetch", fetchMock);
    await api.post("/properties", { name: "New" });
    const [, init] = fetchMock.mock.calls[0];
    expect((init.headers as Record<string, string>)["X-XSRF-TOKEN"]).toBe(
      "abc123",
    );
  });
});
