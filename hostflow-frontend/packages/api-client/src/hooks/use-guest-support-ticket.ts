import { useMutation } from "@tanstack/react-query";
import { api } from "../http-client";
import type { SupportTicketResponse, CreateSupportTicketRequest } from "@hostflow/types";

// POST /api/v1/crm/support-tickets, productScope fixed to NAZILCO — the
// controller is isAuthenticated() only (not PRODUCT_XANUOS-gated), so a
// logged-in guest can raise a ticket directly; no contactId since guests
// don't have a CRM contact record.
export function useRaiseSupportTicket() {
  return useMutation({
    mutationFn: (request: Omit<CreateSupportTicketRequest, "productScope">) =>
      api.post<SupportTicketResponse>("/crm/support-tickets", {
        ...request,
        productScope: "NAZILCO",
      }),
  });
}
