import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { BookingResponse } from "@hostflow/types";
import type { GuestBookingFormValues } from "@hostflow/validation";

// Guest-initiated booking creation, hitting GuestBookingController's separate
// /bookings/public path (not module-booking's staff-facing /bookings —
// distinct controllers, per the backend's own doc comment, to avoid an
// ambiguous mapping collision). CreateBookingRequest is exactly
// {propertyId, checkIn, checkOut, totalPrice} — the guest identity comes
// from the JWT server-side, so totalPrice must be computed client-side from
// the property's basePrice and passed explicitly.
export function useCreateGuestBooking(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: GuestBookingFormValues & { totalPrice: string }) =>
      api.post<BookingResponse>("/bookings/public", {
        propertyId,
        checkIn: values.checkIn,
        checkOut: values.checkOut,
        totalPrice: values.totalPrice,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["public", "bookings", "availability", propertyId],
      });
      queryClient.invalidateQueries({ queryKey: ["guest-portal", "my-bookings"] });
    },
  });
}

export function nightsBetween(checkIn: string, checkOut: string): number {
  const ms = new Date(checkOut).getTime() - new Date(checkIn).getTime();
  return Math.max(0, Math.round(ms / (1000 * 60 * 60 * 24)));
}
