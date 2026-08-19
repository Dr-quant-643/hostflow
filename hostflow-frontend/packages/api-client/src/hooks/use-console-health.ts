import { useQuery } from "@tanstack/react-query";
import { getApiBaseUrl } from "../config";
import type { AggregatedHealthResponse } from "@hostflow/types";

// GET /admin/health (gateway-service's AdminHealthController) lives on the
// gateway root, NOT under /api/v1, and returns AggregatedHealthResponse
// directly — it is not wrapped in the usual ApiResponse<T> envelope, since
// it's a plain proxy of app's own /actuator/health rather than one of the
// app's /api/v1 REST resources. Both of those rule out api.get()/apiFetch,
// so this hook does a raw fetch against the gateway root instead.
function gatewayRootUrl(): string {
  return getApiBaseUrl().replace(/\/api\/v1\/?$/, "");
}

export function useSystemHealth() {
  return useQuery({
    queryKey: ["console", "health"],
    queryFn: async () => {
      const res = await fetch(`${gatewayRootUrl()}/admin/health`, {
        credentials: "include",
      });
      if (!res.ok) {
        throw new Error(`Health check failed with status ${res.status}`);
      }
      return (await res.json()) as AggregatedHealthResponse;
    },
    refetchInterval: 30_000,
  });
}
