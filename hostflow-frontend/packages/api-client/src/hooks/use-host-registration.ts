import { useMutation } from "@tanstack/react-query";
import { api } from "../http-client";
import type { OrganizationResponse, RegisterHostRequest } from "@hostflow/types";

// POST /api/v1/hosts/register — HostSelfSignupController is deliberately
// unauthenticated, the XanuOS counterpart to useRegisterGuest. Only creates
// the organization + owner account; the caller still needs to go through
// /xanuos/api/auth/login afterward to establish a session.
export function useRegisterHost() {
  return useMutation({
    mutationFn: (request: RegisterHostRequest) =>
      api.post<OrganizationResponse>("/hosts/register", request),
  });
}
