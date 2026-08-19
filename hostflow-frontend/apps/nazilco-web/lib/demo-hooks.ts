"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { BookingResponse, GuestInvoiceRow, GuestNotificationRow } from "@hostflow/types";
import {
  DEMO_PROPERTIES,
  DEMO_REVIEWS,
  findDemoProperty,
  type DemoProperty,
  type DemoReview,
} from "./demo-data";
import type {
  PropertySearchParams,
} from "@hostflow/api-client/src/hooks/use-public-properties";

// Demo-mode stand-ins for the real @hostflow/api-client hooks — same
// {data, isLoading, isError} shape, backed by local fixture data instead of
// a network call, with a short artificial delay so skeleton states are
// still visible. Every page importing these can switch back to the real
// hooks (@hostflow/api-client/src/hooks/use-public-properties, etc.) by
// changing only the import path — nothing else about the call sites
// changes, since the return shapes match exactly.

function useDemoQuery<T>(key: unknown[], resolve: () => T, delayMs = 450) {
  return useQuery({
    queryKey: ["demo", ...key],
    queryFn: () => new Promise<T>((resolve_) => setTimeout(() => resolve_(resolve()), delayMs)),
    staleTime: Infinity,
  });
}

export function useDiscoverProperties(limit = 20, offset = 0) {
  return useDemoQuery(["discover", limit, offset], () => DEMO_PROPERTIES.slice(offset, offset + limit));
}

export function useSearchProperties(params: PropertySearchParams | null) {
  return useDemoQuery(
    ["search", params],
    () => {
      if (!params) return [];
      const city = params.city?.toLowerCase().trim();
      if (!city) return DEMO_PROPERTIES;
      return DEMO_PROPERTIES.filter(
        (p) =>
          p.city.toLowerCase().includes(city) ||
          p.country.toLowerCase().includes(city) ||
          p.name.toLowerCase().includes(city),
      );
    },
    500,
  );
}

export function usePublicProperty(id: string) {
  return useDemoQuery(["property", id], () => findDemoProperty(id));
}

export function usePropertyPhotos(id: string) {
  return useDemoQuery(["photos", id], () => findDemoProperty(id)?.photos ?? []);
}

export function useCheckAvailability(propertyId: string, checkIn: string, checkOut: string) {
  return useDemoQuery(
    ["availability", propertyId, checkIn, checkOut],
    () => {
      // Deterministic-but-varied "availability" so the demo isn't always
      // green: a small, stable hash of the date range decides the result.
      const seed = (checkIn + checkOut).split("").reduce((acc, c) => acc + c.charCodeAt(0), 0);
      return seed % 7 !== 0;
    },
    650,
  );
}

export function useDemoReviews(propertyId: string) {
  return useDemoQuery(["reviews", propertyId], () => DEMO_REVIEWS[propertyId] ?? []);
}

function daysFromNow(days: number): string {
  const d = new Date();
  d.setDate(d.getDate() + days);
  return d.toISOString().slice(0, 10);
}

// Mutable in-memory store (module-level, resets on page reload) so the full
// guest flow — search, book, checkout/confirm, cancel, review — is clickable
// end-to-end in demo mode without a backend. Real bookings would live
// server-side; this is purely a presentation-layer stand-in.
let demoBookingIdCounter = 100;

const DEMO_BOOKINGS: BookingResponse[] = [
  {
    id: "d2000000-0000-0000-0000-000000000001",
    propertyId: DEMO_PROPERTIES[0]!.id,
    guestUserId: "demo-guest",
    checkIn: daysFromNow(21),
    checkOut: daysFromNow(28),
    status: "CONFIRMED",
    totalPrice: "378000.00",
  },
  {
    id: "d2000000-0000-0000-0000-000000000002",
    propertyId: DEMO_PROPERTIES[4]!.id,
    guestUserId: "demo-guest",
    checkIn: daysFromNow(-40),
    checkOut: daysFromNow(-33),
    status: "CHECKED_OUT",
    totalPrice: "199500.00",
  },
  {
    id: "d2000000-0000-0000-0000-000000000003",
    propertyId: DEMO_PROPERTIES[2]!.id,
    guestUserId: "demo-guest",
    checkIn: daysFromNow(-90),
    checkOut: daysFromNow(-86),
    status: "CANCELLED",
    totalPrice: "54000.00",
  },
];

export function useDemoMyBookings() {
  return useDemoQuery(["my-bookings"], () => DEMO_BOOKINGS);
}

export function useDemoMyTrips() {
  const query = useDemoMyBookings();
  const now = Date.now();
  const upcoming = (query.data ?? []).filter((b) => new Date(b.checkIn).getTime() >= now);
  const past = (query.data ?? []).filter((b) => new Date(b.checkIn).getTime() < now);
  return { ...query, upcoming, past };
}

export function useDemoMyBooking(id: string) {
  const query = useDemoMyBookings();
  return { ...query, data: query.data?.find((b) => b.id === id) };
}

export function useDemoCreateBooking(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: { checkIn: string; checkOut: string; totalPrice: string }) =>
      new Promise<BookingResponse>((resolve) => {
        setTimeout(() => {
          const booking: BookingResponse = {
            id: `d2000000-0000-0000-0000-${String(demoBookingIdCounter++).padStart(12, "0")}`,
            propertyId,
            guestUserId: "demo-guest",
            checkIn: values.checkIn,
            checkOut: values.checkOut,
            status: "PENDING",
            totalPrice: values.totalPrice,
          };
          DEMO_BOOKINGS.unshift(booking);
          resolve(booking);
        }, 500);
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["demo", "my-bookings"] });
    },
  });
}

function useDemoBookingStatusMutation(bookingId: string, nextStatus: BookingResponse["status"]) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () =>
      new Promise<BookingResponse>((resolve, reject) => {
        setTimeout(() => {
          const booking = DEMO_BOOKINGS.find((b) => b.id === bookingId);
          if (!booking) {
            reject(new Error("Booking not found"));
            return;
          }
          booking.status = nextStatus;
          resolve(booking);
        }, 400);
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["demo", "my-bookings"] });
    },
  });
}

export function useDemoConfirmBooking(bookingId: string) {
  return useDemoBookingStatusMutation(bookingId, "CONFIRMED");
}

export function useDemoCancelBooking(bookingId: string) {
  return useDemoBookingStatusMutation(bookingId, "CANCELLED");
}

export function useDemoSubmitReview() {
  return useMutation({
    mutationFn: (input: { bookingId: string; rating: number; comment?: string }) =>
      new Promise<{ id: string }>((resolve) => {
        setTimeout(() => resolve({ id: `review-${input.bookingId}` }), 400);
      }),
  });
}

const DEMO_INVOICES: GuestInvoiceRow[] = [
  { id: "d3000000-0000-0000-0000-000000000001", amount: "378000.00", dueDate: daysFromNow(14), status: "PENDING" },
  { id: "d3000000-0000-0000-0000-000000000002", amount: "199500.00", dueDate: daysFromNow(-33), status: "PAID" },
  { id: "d3000000-0000-0000-0000-000000000003", amount: "54000.00", dueDate: daysFromNow(-86), status: "REFUNDED" },
];

export function useDemoMyInvoices() {
  return useDemoQuery(["my-invoices"], () => DEMO_INVOICES);
}

const DEMO_NOTIFICATIONS: GuestNotificationRow[] = [
  { id: "d4000000-0000-0000-0000-000000000001", templateCode: "BOOKING_CONFIRMED", channel: "EMAIL", status: "DELIVERED", createdAt: new Date(Date.now() - 1000 * 60 * 60 * 3).toISOString() },
  { id: "d4000000-0000-0000-0000-000000000002", templateCode: "CHECK_IN_REMINDER", channel: "PUSH", status: "DELIVERED", createdAt: new Date(Date.now() - 1000 * 60 * 60 * 26).toISOString() },
  { id: "d4000000-0000-0000-0000-000000000003", templateCode: "REVIEW_REQUEST", channel: "EMAIL", status: "DELIVERED", createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 33).toISOString() },
  { id: "d4000000-0000-0000-0000-000000000004", templateCode: "PAYMENT_RECEIPT", channel: "EMAIL", status: "DELIVERED", createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 34).toISOString() },
];

export function useDemoMyNotifications() {
  return useDemoQuery(["my-notifications"], () => DEMO_NOTIFICATIONS);
}

export type { DemoProperty, DemoReview };
