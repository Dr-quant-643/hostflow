import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { RentalTenantResponse, LeaseResponse, RentPaymentResponse } from "@hostflow/types";
import type { RentalTenantFormValues, LeaseFormValues } from "@hostflow/validation";

export function useRentalTenants(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["rental", "tenants", "list", limit, offset],
    queryFn: () =>
      api.get<RentalTenantResponse[]>("/rental/tenants", { params: { limit, offset } }),
  });
}

export function useCreateRentalTenant() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: RentalTenantFormValues) =>
      api.post<RentalTenantResponse>("/rental/tenants", values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rental", "tenants", "list"] });
    },
  });
}

export function useLeases(propertyId: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["rental", "leases", propertyId, limit, offset],
    queryFn: () =>
      api.get<{ content: LeaseResponse[] }>("/rental/leases", {
        params: { propertyId, limit, offset },
      }),
    enabled: !!propertyId,
  });
}

export function useLease(id: string) {
  return useQuery({
    queryKey: ["rental", "leases", "detail", id],
    queryFn: () => api.get<LeaseResponse>(`/rental/leases/${id}`),
    enabled: !!id,
  });
}

export function useCreateLease() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: LeaseFormValues) => api.post<LeaseResponse>("/rental/leases", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["rental", "leases", variables.propertyId] });
    },
  });
}

export function useActivateLease(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<LeaseResponse>(`/rental/leases/${id}/activate`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rental", "leases", "detail", id] });
      queryClient.invalidateQueries({ queryKey: ["rental", "rent-payments", id] });
    },
  });
}

export function useTerminateLease(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<LeaseResponse>(`/rental/leases/${id}/terminate`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rental", "leases", "detail", id] });
    },
  });
}

export function useRentPayments(leaseId: string) {
  return useQuery({
    queryKey: ["rental", "rent-payments", leaseId],
    queryFn: () =>
      api.get<RentPaymentResponse[]>("/rental/rent-payments", { params: { leaseId } }),
    enabled: !!leaseId,
  });
}

function useUpdateRentPaymentStatus(action: "mark-paid" | "waive", leaseId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (paymentId: string) =>
      api.patch<RentPaymentResponse>(`/rental/rent-payments/${paymentId}/${action}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rental", "rent-payments", leaseId] });
    },
  });
}

export function useMarkRentPaid(leaseId: string) {
  return useUpdateRentPaymentStatus("mark-paid", leaseId);
}

export function useWaiveRentPayment(leaseId: string) {
  return useUpdateRentPaymentStatus("waive", leaseId);
}
