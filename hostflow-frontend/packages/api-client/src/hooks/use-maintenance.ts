import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { WorkOrderResponse, AssetResponse } from "@hostflow/types";
import type {
  WorkOrderFormValues,
  AssetFormValues,
  MaintenanceScheduleFormValues,
} from "@hostflow/validation";

export function useWorkOrders(propertyId: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["maintenance", "work-orders", propertyId, limit, offset],
    queryFn: () =>
      api.get<{ content: WorkOrderResponse[] }>("/maintenance/work-orders", {
        params: { propertyId, limit, offset },
      }),
    enabled: !!propertyId,
  });
}

export function useMyAssignments(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["maintenance", "work-orders", "my-assignments", limit, offset],
    queryFn: () =>
      api.get<{ content: WorkOrderResponse[] }>("/maintenance/work-orders/my-assignments", {
        params: { limit, offset },
      }),
  });
}

export function useWorkOrder(id: string) {
  return useQuery({
    queryKey: ["maintenance", "work-orders", "detail", id],
    queryFn: () => api.get<WorkOrderResponse>(`/maintenance/work-orders/${id}`),
    enabled: !!id,
  });
}

export function useCreateWorkOrder() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: WorkOrderFormValues) =>
      api.post<WorkOrderResponse>("/maintenance/work-orders", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["maintenance", "work-orders", variables.propertyId],
      });
    },
  });
}

export function useAssignTechnician(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (technicianUserId: string) =>
      api.patch<WorkOrderResponse>(`/maintenance/work-orders/${id}/assign`, { technicianUserId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["maintenance", "work-orders", "detail", id] });
    },
  });
}

export function useStartWorkOrder(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<WorkOrderResponse>(`/maintenance/work-orders/${id}/start`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["maintenance", "work-orders", "detail", id] });
    },
  });
}

export function useCompleteWorkOrder(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (resolutionNotes: string) =>
      api.patch<WorkOrderResponse>(`/maintenance/work-orders/${id}/complete`, { resolutionNotes }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["maintenance", "work-orders", "detail", id] });
    },
  });
}

export function useCancelWorkOrder(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<WorkOrderResponse>(`/maintenance/work-orders/${id}/cancel`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["maintenance", "work-orders", "detail", id] });
    },
  });
}

export function useAssets(propertyId: string) {
  return useQuery({
    queryKey: ["maintenance", "assets", propertyId],
    queryFn: () => api.get<AssetResponse[]>("/maintenance/assets", { params: { propertyId } }),
    enabled: !!propertyId,
  });
}

export function useCreateAsset() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: AssetFormValues) => api.post<AssetResponse>("/maintenance/assets", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["maintenance", "assets", variables.propertyId] });
    },
  });
}

export function useDecommissionAsset(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.patch<void>(`/maintenance/assets/${id}/decommission`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["maintenance", "assets", propertyId] });
    },
  });
}

// No list endpoint exists for schedules — create-only, per the real backend.
export function useCreateMaintenanceSchedule() {
  return useMutation({
    mutationFn: (values: MaintenanceScheduleFormValues) =>
      api.post<string>("/maintenance/schedules", values),
  });
}
