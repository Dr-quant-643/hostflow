import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { BookingOversightRow } from "@hostflow/types";

// GET /api/v1/admin/bookings (PlatformAdminController,
// hasRole('PLATFORM_ADMIN')) — backed by PlatformBookingQueries, cross-tenant
// via platformAdminJdbcTemplate. Plain array (limit/offset).
export function useAllGuestBookings(status?: string, limit = 50, offset = 0) {
  return useQuery({
    queryKey: ["nazilco-admin", "bookings", "list", status, limit, offset],
    queryFn: () =>
      api.get<BookingOversightRow[]>("/admin/bookings", {
        params: { status, limit, offset },
      }),
  });
}
