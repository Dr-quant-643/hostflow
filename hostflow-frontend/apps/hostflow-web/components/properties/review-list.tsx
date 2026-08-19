"use client";

import { useState } from "react";
import { Stack, Card, Badge, Button, Textarea, Skeleton, EmptyState, toast } from "@hostflow/ui";
import { useReviews, useRespondToReview } from "@hostflow/api-client/src/hooks/use-reviews";

function ReviewRow({ propertyId, review }: { propertyId: string; review: { id: string; rating: number; comment: string | null; ownerResponse: string | null } }) {
  const [response, setResponse] = useState("");
  const respond = useRespondToReview(propertyId);

  return (
    <Card>
      <Stack gap="sm">
        <Stack direction="row" gap="sm" align="center">
          <Badge>{review.rating} / 5</Badge>
        </Stack>
        {review.comment && <p className="text-sm">{review.comment}</p>}
        {review.ownerResponse ? (
          <p className="text-sm text-muted-foreground">Response: {review.ownerResponse}</p>
        ) : (
          <Stack direction="row" gap="sm" align="end">
            <Textarea
              value={response}
              onChange={(e) => setResponse(e.target.value)}
              placeholder="Write a response to this review..."
            />
            <Button
              disabled={!response.trim()}
              loading={respond.isPending}
              onClick={async () => {
                try {
                  await respond.mutateAsync({ id: review.id, response });
                  toast.success("Response posted");
                } catch {
                  toast.error("Failed to post response");
                }
              }}
            >
              Respond
            </Button>
          </Stack>
        )}
      </Stack>
    </Card>
  );
}

export function ReviewList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useReviews(propertyId);

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load reviews" description="Try refreshing." />;
  }
  if (!data || data.content.length === 0) {
    return <EmptyState title="No reviews yet" />;
  }

  return (
    <Stack gap="sm">
      {data.content.map((review) => (
        <ReviewRow key={review.id} propertyId={propertyId} review={review} />
      ))}
    </Stack>
  );
}
