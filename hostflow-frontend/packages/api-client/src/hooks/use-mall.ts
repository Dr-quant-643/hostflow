import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type {
  RetailUnitResponse,
  RetailTenantResponse,
  MallEventResponse,
  ParkingSessionResponse,
} from "@hostflow/types";
import type {
  RetailUnitFormValues,
  AssignRetailTenantFormValues,
  MallEventFormValues,
  ParkingEntryFormValues,
} from "@hostflow/validation";

export function useRetailUnits(propertyId: string) {
  return useQuery({
    queryKey: ["mall", "retail-units", propertyId],
    queryFn: () => api.get<RetailUnitResponse[]>("/mall/retail-units", { params: { propertyId } }),
    enabled: !!propertyId,
  });
}

export function useCreateRetailUnit() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: RetailUnitFormValues) =>
      api.post<RetailUnitResponse>("/mall/retail-units", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["mall", "retail-units", variables.propertyId] });
    },
  });
}

export function useAssignRetailTenant(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: AssignRetailTenantFormValues) =>
      api.post<RetailTenantResponse>("/mall/retail-units/assign-tenant", values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["mall", "retail-units", propertyId] });
    },
  });
}

export function useMallEvents(propertyId: string) {
  return useQuery({
    queryKey: ["mall", "events", propertyId],
    queryFn: () => api.get<MallEventResponse[]>("/mall/events", { params: { propertyId } }),
    enabled: !!propertyId,
  });
}

export function useCreateMallEvent() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: MallEventFormValues) =>
      api.post<MallEventResponse>("/mall/events", {
        ...values,
        startsAt: new Date(values.startsAt).toISOString(),
        endsAt: new Date(values.endsAt).toISOString(),
      }),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["mall", "events", variables.propertyId] });
    },
  });
}

// No list endpoint exists for parking sessions — entry/exit only, per the real
// backend contract.
export function useParkingEntry() {
  return useMutation({
    mutationFn: (values: ParkingEntryFormValues) =>
      api.post<ParkingSessionResponse>("/mall/parking/enter", values),
  });
}

export function useParkingExit() {
  return useMutation({
    mutationFn: (id: string) => api.patch<ParkingSessionResponse>(`/mall/parking/${id}/exit`),
  });
}
