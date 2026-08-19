import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { PlatformUserRow } from "@hostflow/types";

// GET /api/v1/admin/platform-users (PlatformAdminController,
// hasRole('PLATFORM_ADMIN')) — backed by PlatformOrganizationQueries, a
// plain cross-tenant array (limit/offset). There is no server-side search
// endpoint (no /organizations/users/search anywhere in the backend), so
// filtering by name/email happens client-side over this list.
export function usePlatformUsers(limit = 100, offset = 0) {
  return useQuery({
    queryKey: ["console", "users", "list", limit, offset],
    queryFn: () =>
      api.get<PlatformUserRow[]>("/admin/platform-users", { params: { limit, offset } }),
  });
}

export function useFilteredPlatformUsers(query: string, limit = 100, offset = 0) {
  const result = usePlatformUsers(limit, offset);
  const q = query.trim().toLowerCase();
  const data = !q
    ? result.data
    : result.data?.filter(
        (u) =>
          u.email.toLowerCase().includes(q) ||
          `${u.firstName} ${u.lastName}`.toLowerCase().includes(q),
      );
  return { ...result, data };
}
