import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { BookingResponse } from "@hostflow/types";
import { useMyBooking } from "./use-guest-portal";

// There is no GET /bookings/public/{id} — GuestBookingController only
// exposes create/mine/confirm/cancel. A just-created booking is already in
// the guest's /bookings/public/mine list, so checkout looks it up from
// there rather than hitting a single-booking endpoint that doesn't exist.
export function useBookingForCheckout(bookingId: string) {
  return useMyBooking(bookingId);
}

// "Checkout" for a guest booking is PATCH .../confirm — there is no separate
// payment/checkout endpoint (no processor integration anywhere in the
// backend), so confirming is the real terminal action here.
export function useConfirmCheckout(bookingId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () =>
      api.patch<BookingResponse>(`/bookings/public/${bookingId}/confirm`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["guest-portal", "my-bookings"] });
    },
  });
}
