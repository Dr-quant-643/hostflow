import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { ReviewResponse } from "@hostflow/types";

export function useReviews(propertyId: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["reviews", propertyId, limit, offset],
    queryFn: () =>
      api.get<{ content: ReviewResponse[] }>("/reviews", {
        params: { propertyId, limit, offset },
      }),
    enabled: !!propertyId,
  });
}

export function useRespondToReview(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, response }: { id: string; response: string }) =>
      api.patch<ReviewResponse>(`/reviews/${id}/respond`, { response }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["reviews", propertyId] });
    },
  });
}
