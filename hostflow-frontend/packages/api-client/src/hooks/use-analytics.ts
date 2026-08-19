import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { PropertyOccupancyResponse, MonthlyRevenueResponse } from "@hostflow/types";

// Backend only exposes these two report endpoints (module-analytics) — both
// return a plain list, no pagination, no trend/rate endpoints exist.

export function usePropertyOccupancy() {
  return useQuery({
    queryKey: ["analytics", "property-occupancy"],
    queryFn: () => api.get<PropertyOccupancyResponse[]>("/analytics/property-occupancy"),
  });
}

export function useMonthlyRevenue() {
  return useQuery({
    queryKey: ["analytics", "monthly-revenue"],
    queryFn: () => api.get<MonthlyRevenueResponse[]>("/analytics/monthly-revenue"),
  });
}
