import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type {
  ApiKeyResponse,
  CreateApiKeyRequest,
  CreateApiKeyResponse,
  WebhookSubscriptionResponse,
  CreateWebhookSubscriptionRequest,
} from "@hostflow/types";

// API keys -- the raw key is only ever returned once, from useCreateApiKey's
// mutation result (CreateApiKeyResponse). The list endpoint (ApiKeyResponse)
// only ever carries a display prefix.

export function useApiKeys() {
  return useQuery({
    queryKey: ["api-keys", "list"],
    queryFn: () => api.get<ApiKeyResponse[]>("/api-keys"),
  });
}

export function useCreateApiKey() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateApiKeyRequest) => api.post<CreateApiKeyResponse>("/api-keys", request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["api-keys", "list"] });
    },
  });
}

export function useRevokeApiKey() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.patch<ApiKeyResponse>(`/api-keys/${id}/revoke`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["api-keys", "list"] });
    },
  });
}

// Webhooks

export function useWebhookSubscriptions() {
  return useQuery({
    queryKey: ["webhooks", "list"],
    queryFn: () => api.get<WebhookSubscriptionResponse[]>("/webhooks"),
  });
}

export function useCreateWebhookSubscription() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateWebhookSubscriptionRequest) =>
      api.post<WebhookSubscriptionResponse>("/webhooks", request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["webhooks", "list"] });
    },
  });
}

export function useDeactivateWebhookSubscription() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.delete<void>(`/webhooks/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["webhooks", "list"] });
    },
  });
}
