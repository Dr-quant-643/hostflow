import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { Campaign } from "@hostflow/types";
import type { CampaignFormValues } from "@hostflow/validation";

export function useCampaigns(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["marketing", "campaigns", "list", limit, offset],
    queryFn: () =>
      api.get<Campaign[]>("/marketing/campaigns", { params: { limit, offset } }),
  });
}

export function useCampaign(id: string) {
  return useQuery({
    queryKey: ["marketing", "campaigns", "detail", id],
    queryFn: () => api.get<Campaign>(`/marketing/campaigns/${id}`),
    enabled: !!id,
  });
}

export function useCreateCampaign() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: CampaignFormValues) =>
      api.post<Campaign>("/marketing/campaigns", values),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["marketing", "campaigns", "list"],
      });
    },
  });
}

export function useUpdateCampaignContent(campaignId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (content: string) =>
      api.patch<Campaign>(`/marketing/campaigns/${campaignId}/content`, { content }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["marketing", "campaigns", "detail", campaignId],
      });
    },
  });
}

export function usePublishCampaign(campaignId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () =>
      api.patch<Campaign>(`/marketing/campaigns/${campaignId}/publish`),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["marketing", "campaigns", "detail", campaignId],
      });
    },
  });
}

export function useArchiveCampaign(campaignId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () =>
      api.patch<Campaign>(`/marketing/campaigns/${campaignId}/archive`),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["marketing", "campaigns", "detail", campaignId],
      });
      queryClient.invalidateQueries({
        queryKey: ["marketing", "campaigns", "list"],
      });
    },
  });
}
