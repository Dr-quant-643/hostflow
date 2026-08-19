import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { User, PageResponse } from "@hostflow/types";

// Grounded in something real this time — module-identity (User, Keycloak
// provisioning) and core-security (JWT converter, product scope authority)
// both exist in the completed backend. Listing/editing a user's authorities
// is a reasonable extension of what's already there, unlike Products.
export function useUsers(page = 0, size = 20) {
  return useQuery({
    queryKey: ["admin", "access", "users", page, size],
    queryFn: () =>
      api.get<PageResponse<User>>("/organizations/users", {
        params: { page, size },
      }),
  });
}

export function useUpdateUserAuthorities(userId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (authorities: string[]) =>
      api.patch<User>(`/organizations/users/${userId}/authorities`, {
        authorities,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "access", "users"] });
    },
  });
}
