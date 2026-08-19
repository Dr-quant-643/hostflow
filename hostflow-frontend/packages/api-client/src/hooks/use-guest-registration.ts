import { useMutation } from "@tanstack/react-query";
import { api } from "../http-client";
import type { GuestProfileResponse, RegisterGuestRequest } from "@hostflow/types";

// POST /api/v1/guests/register — GuestRegistrationController is deliberately
// unauthenticated (the one identity-creation endpoint reachable anonymously).
// This only creates the guest profile/Keycloak account; the caller still
// needs to go through /api/auth/login afterward to establish a session.
export function useRegisterGuest() {
  return useMutation({
    mutationFn: (request: RegisterGuestRequest) =>
      api.post<GuestProfileResponse>("/guests/register", request),
  });
}
