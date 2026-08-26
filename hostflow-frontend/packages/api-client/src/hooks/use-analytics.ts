import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { PropertyOccupancyResponse, MonthlyRevenueResponse, GuestSegmentRow, PricingSuggestionRow } from "@hostflow/types";

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

export function useGuestSegments() {
  return useQuery({
    queryKey: ["analytics", "guest-segments"],
    queryFn: () => api.get<GuestSegmentRow[]>("/analytics/guest-segments"),
  });
}

export function usePricingSuggestions() {
  return useQuery({
    queryKey: ["analytics", "pricing-suggestions"],
    queryFn: () => api.get<PricingSuggestionRow[]>("/analytics/pricing-suggestions"),
  });
}

// Guest-facing: a guest's own segment status with the specific owner of
// `propertyId` -- inherently per-owner, not global, same reasoning as
// GuestSegmentQueries itself never building a cross-tenant guest profile.
export interface MyLoyaltyStatus {
  segment: "ACTIVE_TENANT" | "AT_RISK" | "VIP" | "REPEAT" | "NEW";
  totalStays: number;
}

export function useMyLoyaltyStatus(propertyId: string) {
  return useQuery({
    queryKey: ["analytics", "my-loyalty-status", propertyId],
    queryFn: () => api.get<MyLoyaltyStatus>("/analytics/my-loyalty-status", { params: { propertyId } }),
    enabled: !!propertyId,
  });
}
