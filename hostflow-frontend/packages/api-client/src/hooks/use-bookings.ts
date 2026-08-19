import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { BookingResponse } from "@hostflow/types";

// Staff-side (XanuOS) booking oversight. Bookings are guest-initiated only —
// there is no staff-facing create endpoint (POST /bookings requires
// PRODUCT_NAZILCO) — and lifecycle only exposes confirm/cancel, not a
// generic status transition or check-in/check-out actions.

export function useBookings(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["bookings", "list", limit, offset],
    queryFn: () =>
      api.get<BookingResponse[]>("/bookings", { params: { limit, offset } }),
  });
}

export function useBooking(id: string) {
  return useQuery({
    queryKey: ["bookings", "detail", id],
    queryFn: () => api.get<BookingResponse>(`/bookings/${id}`),
    enabled: !!id,
  });
}

export function useConfirmBooking(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<BookingResponse>(`/bookings/${id}/confirm`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bookings", "list"] });
      queryClient.invalidateQueries({ queryKey: ["bookings", "detail", id] });
    },
  });
}

export function useCancelBooking(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<BookingResponse>(`/bookings/${id}/cancel`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bookings", "list"] });
      queryClient.invalidateQueries({ queryKey: ["bookings", "detail", id] });
    },
  });
}
