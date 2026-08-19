import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { PageResponse } from "@hostflow/types";

// No NotificationLog type was listed among Module 4's created files —
// defining a minimal local shape here rather than assuming one in
// @hostflow/types. Reconcile with a real type once one exists.
export interface NotificationLogEntry {
  id: string;
  channel: "EMAIL" | "SMS" | "WHATSAPP";
  subject: string;
  status: "SIMULATED" | "SENT" | "FAILED";
  createdAt: string;
  read: boolean;
}

export function useNotifications(page = 0, size = 20) {
  return useQuery({
    queryKey: ["notifications", "list", page, size],
    queryFn: () =>
      api.get<PageResponse<NotificationLogEntry>>("/notifications", {
        params: { page, size },
      }),
  });
}

export function useMarkNotificationRead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.patch<void>(`/notifications/${id}/read`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications", "list"] });
    },
  });
}
