import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { SegmentCampaignResponse, CreateSegmentCampaignRequest } from "@hostflow/types";

// Closes the loop on guest segmentation (useGuestSegments) -- write a
// message once, send it to every guest currently in a segment. Backed by
// SegmentCampaignController (app/publicapi), a separate top-level route
// from /analytics since it's a write-side feature, not a report.

export function useSegmentCampaigns() {
  return useQuery({
    queryKey: ["segment-campaigns", "list"],
    queryFn: () => api.get<SegmentCampaignResponse[]>("/segment-campaigns"),
  });
}

export function useCreateSegmentCampaign() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateSegmentCampaignRequest) =>
      api.post<SegmentCampaignResponse>("/segment-campaigns", request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["segment-campaigns", "list"] });
    },
  });
}

export function useSendSegmentCampaign(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<SegmentCampaignResponse>(`/segment-campaigns/${id}/send`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["segment-campaigns", "list"] });
    },
  });
}
