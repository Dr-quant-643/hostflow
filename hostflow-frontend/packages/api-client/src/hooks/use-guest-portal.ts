import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { BookingResponse } from "@hostflow/types";

// GuestBookingOrchestrator.myBookings returns a plain list (ordered by
// check_in DESC) — no pagination on the guest-facing endpoint, unlike the
// staff-side booking list.
export function useMyBookings() {
  return useQuery({
    queryKey: ["guest-portal", "my-bookings"],
    queryFn: () => api.get<BookingResponse[]>("/bookings/public/mine"),
  });
}

function isUpcoming(booking: BookingResponse): boolean {
  return new Date(booking.checkIn).getTime() >= Date.now();
}

// Splitting upcoming/past client-side rather than as two separate backend
// calls — one fetch, two derived views.
export function useMyTrips() {
  const query = useMyBookings();
  const upcoming = (query.data ?? []).filter(isUpcoming);
  const past = (query.data ?? []).filter((b) => !isUpcoming(b));
  return { ...query, upcoming, past };
}

export function useMyBooking(id: string) {
  const query = useMyBookings();
  return {
    ...query,
    data: query.data?.find((b) => b.id === id),
  };
}
