import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { GuestNotificationRow } from "@hostflow/types";

// GET /api/v1/notifications/mine — NotificationInboxController routes a
// tenant-less PRODUCT_NAZILCO caller to GuestNotificationQueries, returning
// a plain array (limit/offset, not page/size).
export function useMyNotifications(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["guest-portal", "my-notifications", limit, offset],
    queryFn: () =>
      api.get<GuestNotificationRow[]>("/notifications/mine", {
        params: { limit, offset },
      }),
  });
}
