import { useMutation } from "@tanstack/react-query";
import { api } from "../http-client";
import type { ClaimWorkspaceRequest, OrganizationResponse } from "@hostflow/types";

// POST /api/v1/hosts/claim-workspace — requires an existing session (unlike
// useRegisterHost/useRegisterGuest). For a Keycloak identity created via
// "Continue with Google" that has no organization yet. The caller's current
// session cookie still carries the OLD claims after this succeeds — the
// access token must be refreshed (see /api/auth/refresh) before
// PRODUCT_XANUOS actually takes effect.
export function useClaimWorkspace() {
  return useMutation({
    mutationFn: (request: ClaimWorkspaceRequest) =>
      api.post<OrganizationResponse>("/hosts/claim-workspace", request),
  });
}
