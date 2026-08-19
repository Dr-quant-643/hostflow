import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { ReviewResponse, CreateReviewRequest } from "@hostflow/types";

// POST /api/v1/reviews (GuestReviewController) — the only review-related
// endpoint reachable by a guest; there is no GET for a guest to read reviews
// back, so this hook is create-only.
export function useSubmitReview() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateReviewRequest) =>
      api.post<ReviewResponse>("/reviews", request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["guest-portal", "my-bookings"] });
    },
  });
}
