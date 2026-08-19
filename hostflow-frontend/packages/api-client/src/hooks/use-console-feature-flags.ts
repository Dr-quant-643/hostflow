import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { FeatureFlagResponse } from "@hostflow/types";

// module-platform-admin's FeatureFlagController, hasRole('PLATFORM_ADMIN').
export function useFeatureFlags() {
  return useQuery({
    queryKey: ["console", "feature-flags"],
    queryFn: () => api.get<FeatureFlagResponse[]>("/admin/feature-flags"),
  });
}

export function useSetGlobalFlag() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      key,
      enabled,
      description,
    }: {
      key: string;
      enabled: boolean;
      description?: string;
    }) => api.put<FeatureFlagResponse>("/admin/feature-flags", { key, enabled, description }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["console", "feature-flags"] });
    },
  });
}

export function useSetOrgFlagOverride() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ orgId, key, enabled }: { orgId: string; key: string; enabled: boolean }) =>
      api.put<FeatureFlagResponse>(`/admin/feature-flags/org/${orgId}`, undefined, {
        params: { key, enabled },
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["console", "feature-flags"] });
    },
  });
}
