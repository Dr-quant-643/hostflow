import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { MonitoringSnapshot } from "@hostflow/types";

// GET /api/v1/admin/monitoring (PlatformMonitoringController,
// hasRole('PLATFORM_ADMIN')) — minimal DB-backed business-metric counters,
// distinct from System Health (service up/down) and from real
// Prometheus/Grafana metrics (out of scope here by design).
export function usePlatformMonitoring() {
  return useQuery({
    queryKey: ["console", "monitoring"],
    queryFn: () => api.get<MonitoringSnapshot>("/admin/monitoring"),
    refetchInterval: 30_000,
  });
}
