import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type {
  SupportTicketResponse,
  TicketProductScope,
  TicketStatus,
  PageResponse,
} from "@hostflow/types";

// SupportTicketController is real (module-crm) — a genuine SupportTicket
// entity, not CRM interactions repurposed. isAuthenticated() at class level;
// list requires productScope explicitly so hostflow-admin only ever sees
// XANUOS tickets and nazilco-admin only ever sees NAZILCO ones. Backend
// returns Spring Data's Page<SupportTicketResponse> — content/totalElements/
// first/last line up with our PageResponse<T>, so it's reused here.
export function useSupportTickets(
  productScope: TicketProductScope,
  status?: TicketStatus,
  limit = 20,
  offset = 0,
) {
  return useQuery({
    queryKey: ["support-tickets", productScope, status, limit, offset],
    queryFn: () =>
      api.get<PageResponse<SupportTicketResponse>>("/crm/support-tickets", {
        params: { productScope, status, limit, offset },
      }),
  });
}

function useTicketAction(action: "assign" | "resolve" | "reopen" | "close") {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, staffUserId }: { id: string; staffUserId?: string }) =>
      action === "assign"
        ? api.patch<SupportTicketResponse>(`/crm/support-tickets/${id}/assign`, {
            staffUserId,
          })
        : api.patch<SupportTicketResponse>(`/crm/support-tickets/${id}/${action}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["support-tickets"] });
    },
  });
}

export function useAssignTicket() {
  return useTicketAction("assign");
}
export function useResolveTicket() {
  return useTicketAction("resolve");
}
export function useReopenTicket() {
  return useTicketAction("reopen");
}
export function useCloseTicket() {
  return useTicketAction("close");
}
