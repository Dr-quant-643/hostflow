import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { OrgUserSummaryResponse, UserRole } from "@hostflow/types";

export function useMyOrgUsers(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["team", "list", limit, offset],
    queryFn: () =>
      api.get<{ content: OrgUserSummaryResponse[] }>("/my-organization/users", {
        params: { limit, offset },
      }),
  });
}

export function useSearchMyOrgUsers(q: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["team", "search", q, limit, offset],
    queryFn: () =>
      api.get<{ content: OrgUserSummaryResponse[] }>("/my-organization/users/search", {
        params: { q, limit, offset },
      }),
    enabled: !!q,
  });
}

export function useUpdateUserRoles() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, roles }: { userId: string; roles: UserRole[] }) =>
      api.patch<OrgUserSummaryResponse>(`/my-organization/users/${userId}/roles`, { roles }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["team"] });
    },
  });
}

export function useDeactivateUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userId: string) =>
      api.patch<void>(`/my-organization/users/${userId}/deactivate`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["team"] });
    },
  });
}
