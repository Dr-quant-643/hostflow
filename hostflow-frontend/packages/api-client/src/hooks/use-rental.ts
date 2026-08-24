import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type {
  RentalTenantResponse,
  LeaseResponse,
  RentPaymentResponse,
  RentalInquiryResponse,
  MyRentalInquiry,
} from "@hostflow/types";
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

// Guest-initiated inquiry on a MONTHLY-classified property -- distinct from
// the Lease CRUD hooks above, which are staff-facing. Mirrors
// useCreateGuestBooking's shape in use-public-booking.ts: hits the guest
// PRODUCT_NAZILCO-gated endpoint, notifies the owner server-side, and does
// not create any booking/lease record itself -- the owner follows up and
// creates the formal Lease through the existing staff flow.
export function useSendRentalInquiry(propertyId: string) {
  return useMutation({
    mutationFn: (message?: string) =>
      api.post<void>("/rental/inquiries", { propertyId, message }),
  });
}

// Guest's own sent inquiries + any owner reply -- the in-app alternative to
// finding out "did they answer" only via email.
export function useMyRentalInquiries() {
  return useQuery({
    queryKey: ["rental", "inquiries", "mine"],
    queryFn: () => api.get<MyRentalInquiry[]>("/rental/inquiries/mine"),
  });
}

// Owner-facing: inquiries received on one of their properties, with the full
// message text (unlike the generic /notifications inbox, which only ever
// carried template code/channel/status).
export function useRentalInquiries(propertyId: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["rental", "inquiries", propertyId, limit, offset],
    queryFn: () =>
      api.get<{ content: RentalInquiryResponse[] }>("/rental/inquiries", {
        params: { propertyId, limit, offset },
      }),
    enabled: !!propertyId,
  });
}

// Owner-facing global FIFO queue across ALL of their properties -- backs the
// Notifications tab so replying doesn't require hunting down which property
// an inquiry came from first.
export function useOwnerRentalInquiries() {
  return useQuery({
    queryKey: ["rental", "inquiries", "mine-as-owner"],
    queryFn: () => api.get<MyRentalInquiry[]>("/rental/inquiries/mine-as-owner"),
  });
}

export function useReplyToRentalInquiry(propertyId?: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, message }: { id: string; message: string }) =>
      api.patch<RentalInquiryResponse>(`/rental/inquiries/${id}/reply`, { message }),
    onSuccess: () => {
      if (propertyId) {
        queryClient.invalidateQueries({ queryKey: ["rental", "inquiries", propertyId] });
      }
      queryClient.invalidateQueries({ queryKey: ["rental", "inquiries", "mine-as-owner"] });
    },
  });
}
