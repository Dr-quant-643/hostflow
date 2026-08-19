import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { OrganizationRow, OrganizationResponse, RenameOrganizationRequest } from "@hostflow/types";

// GET /api/v1/organizations (PlatformAdminController, hasRole('PLATFORM_ADMIN'))
// — backed by PlatformOrganizationQueries, a plain cross-tenant array
// (limit/offset). There is no GET /organizations/{id} single-org detail
// endpoint anywhere in the backend, so detail lookups derive from this list.
export function useOrganizations(limit = 50, offset = 0) {
  return useQuery({
    queryKey: ["console", "organizations", "list", limit, offset],
    queryFn: () =>
      api.get<OrganizationRow[]>("/organizations", { params: { limit, offset } }),
  });
}

export function useOrganization(id: string) {
  const query = useOrganizations();
  return {
    ...query,
    data: query.data?.find((o) => o.id === id),
  };
}

// PATCH /api/v1/organizations/{orgId}/rename (module-identity's
// OrganizationController) — the ONLY mutation this backend exposes for an
// existing organization. There is no suspend/deactivate endpoint.
export function useRenameOrganization() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ orgId, name }: { orgId: string } & RenameOrganizationRequest) =>
      api.patch<OrganizationResponse>(`/organizations/${orgId}/rename`, { name }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["console", "organizations"] });
    },
  });
}
