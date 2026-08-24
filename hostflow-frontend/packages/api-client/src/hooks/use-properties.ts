import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { PropertyResponse, UpdatePropertyDetailsRequest } from "@hostflow/types";
import type { PropertyFormValues } from "@hostflow/validation";

export function useProperties(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["properties", "list", limit, offset],
    queryFn: () =>
      api.get<PropertyResponse[]>("/properties", { params: { limit, offset } }),
  });
}

export function useProperty(id: string) {
  return useQuery({
    queryKey: ["properties", "detail", id],
    queryFn: () => api.get<PropertyResponse>(`/properties/${id}`),
    enabled: !!id,
  });
}

export function useCreateProperty() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: PropertyFormValues) =>
      api.post<PropertyResponse>("/properties", values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "list"] });
    },
  });
}

// PATCH /api/v1/properties/{id} — the entity always had updateDescription()/
// updateBasePrice()/updateLocation(), but no controller path ever called
// them until now: a property could be created, published, and archived, but
// never priced or placed on a map.
export function useUpdatePropertyDetails(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: UpdatePropertyDetailsRequest) =>
      api.patch<PropertyResponse>(`/properties/${id}`, values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "list"] });
      queryClient.invalidateQueries({ queryKey: ["properties", "detail", id] });
    },
  });
}

export function usePublishProperty(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<PropertyResponse>(`/properties/${id}/publish`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "list"] });
      queryClient.invalidateQueries({ queryKey: ["properties", "detail", id] });
    },
  });
}

export function useArchiveProperty(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<PropertyResponse>(`/properties/${id}/archive`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "list"] });
      queryClient.invalidateQueries({ queryKey: ["properties", "detail", id] });
    },
  });
}

// Lands on DRAFT, not straight back to ACTIVE -- the owner reviews/updates
// details, then uses the existing Publish action to re-publish.
export function useUnarchiveProperty(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<PropertyResponse>(`/properties/${id}/unarchive`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "list"] });
      queryClient.invalidateQueries({ queryKey: ["properties", "detail", id] });
    },
  });
}

// Owner/manager-set "in use" override, independent of Booking/Lease data --
// see Property.manualOccupiedUntil's javadoc. Purely informational on
// NazilCo, doesn't affect publish status.
export function useSetPropertyOccupancy(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (until: string) =>
      api.patch<PropertyResponse>(`/properties/${id}/occupancy`, { until }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "detail", id] });
    },
  });
}

export function useClearPropertyOccupancy(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.delete<PropertyResponse>(`/properties/${id}/occupancy`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", "detail", id] });
    },
  });
}
