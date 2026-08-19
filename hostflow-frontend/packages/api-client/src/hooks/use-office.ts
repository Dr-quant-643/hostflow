import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { MeetingRoomResponse, RoomBookingResponse, VisitorResponse } from "@hostflow/types";
import type {
  MeetingRoomFormValues,
  RoomBookingFormValues,
  VisitorFormValues,
} from "@hostflow/validation";

export function useMeetingRooms(propertyId: string) {
  return useQuery({
    queryKey: ["office", "rooms", propertyId],
    queryFn: () => api.get<MeetingRoomResponse[]>("/office/rooms", { params: { propertyId } }),
    enabled: !!propertyId,
  });
}

export function useCreateMeetingRoom() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: MeetingRoomFormValues) =>
      api.post<MeetingRoomResponse>("/office/rooms", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["office", "rooms", variables.propertyId] });
    },
  });
}

// No list endpoint exists for room bookings — create/cancel only, per the real
// backend contract.
export function useCreateRoomBooking() {
  return useMutation({
    mutationFn: (values: RoomBookingFormValues) =>
      api.post<RoomBookingResponse>("/office/room-bookings", {
        ...values,
        startsAt: new Date(values.startsAt).toISOString(),
        endsAt: new Date(values.endsAt).toISOString(),
      }),
  });
}

export function useCancelRoomBooking() {
  return useMutation({
    mutationFn: (id: string) => api.patch<RoomBookingResponse>(`/office/room-bookings/${id}/cancel`),
  });
}

export function useVisitors(propertyId: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["office", "visitors", propertyId, limit, offset],
    queryFn: () =>
      api.get<{ content: VisitorResponse[] }>("/office/visitors", {
        params: { propertyId, limit, offset },
      }),
    enabled: !!propertyId,
  });
}

export function useRegisterVisitor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: VisitorFormValues) =>
      api.post<VisitorResponse>("/office/visitors", {
        ...values,
        expectedAt: new Date(values.expectedAt).toISOString(),
      }),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["office", "visitors", variables.propertyId] });
    },
  });
}

function useUpdateVisitorStatus(action: "check-in" | "check-out", propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.patch<VisitorResponse>(`/office/visitors/${id}/${action}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["office", "visitors", propertyId] });
    },
  });
}

export function useCheckInVisitor(propertyId: string) {
  return useUpdateVisitorStatus("check-in", propertyId);
}

export function useCheckOutVisitor(propertyId: string) {
  return useUpdateVisitorStatus("check-out", propertyId);
}
