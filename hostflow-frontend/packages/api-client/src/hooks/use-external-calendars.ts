import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { ExternalCalendarLinkResponse, CreateExternalCalendarLinkRequest } from "@hostflow/types";

// The free half of iCal channel sync (import direction) -- an owner pastes
// their Airbnb/Booking.com/VRBO .ics export URL here; ExternalCalendarSyncJob
// polls it periodically and blocks those dates on NazilCo. The export
// direction (this property's own calendar.ics feed to paste INTO those
// platforms) has no owner-facing mutation -- it's just a public URL, built
// client-side from the property id.

export function useExternalCalendarLinks(propertyId: string) {
  return useQuery({
    queryKey: ["bookings", "external-calendars", propertyId],
    queryFn: () => api.get<ExternalCalendarLinkResponse[]>("/bookings/external-calendars", { params: { propertyId } }),
    enabled: !!propertyId,
  });
}

export function useCreateExternalCalendarLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateExternalCalendarLinkRequest) =>
      api.post<ExternalCalendarLinkResponse>("/bookings/external-calendars", request),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["bookings", "external-calendars", variables.propertyId] });
    },
  });
}

export function useDeleteExternalCalendarLink(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.delete<void>(`/bookings/external-calendars/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bookings", "external-calendars", propertyId] });
    },
  });
}
