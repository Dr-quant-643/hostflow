import { useMutation } from "@tanstack/react-query";
import { api } from "../http-client";
import type { GuestProfileResponse } from "@hostflow/types";

// POST /api/v1/guests/claim-profile — requires an existing session (unlike
// useRegisterGuest). For a Keycloak identity created via "Continue with
// Google" that has no NazilCo product_scope yet. The caller's current
// session cookie still carries the OLD claims after this succeeds — the
// access token must be refreshed (see /api/auth/refresh) before
// PRODUCT_NAZILCO actually takes effect.
export function useClaimGuestProfile() {
  return useMutation({
    mutationFn: () => api.post<GuestProfileResponse>("/guests/claim-profile"),
  });
}
