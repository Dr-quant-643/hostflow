import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { BookingResponse } from "@hostflow/types";

export function useCancelGuestBooking(bookingId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () =>
      api.patch<BookingResponse>(`/bookings/public/${bookingId}/cancel`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["guest-portal", "my-bookings"] });
    },
  });
}
