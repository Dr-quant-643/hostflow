"use client";

import { Stack, Badge, Button, Skeleton, EmptyState, Card, toast } from "@hostflow/ui";
import {
  useVisitors,
  useCheckInVisitor,
  useCheckOutVisitor,
} from "@hostflow/api-client/src/hooks/use-office";

export function VisitorList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useVisitors(propertyId);
  const checkIn = useCheckInVisitor(propertyId);
  const checkOut = useCheckOutVisitor(propertyId);

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load visitors" description="Try refreshing." />;
  }
  if (!data || data.content.length === 0) {
    return <EmptyState title="No visitors registered yet" />;
  }

  return (
    <Stack gap="sm">
      {data.content.map((visitor) => (
        <Card key={visitor.id}>
          <Stack direction="row" justify="between" align="center">
            <Stack direction="row" gap="sm" align="center">
              <Badge>{visitor.status}</Badge>
              <span className="text-sm font-medium">{visitor.fullName}</span>
              {visitor.company && (
                <span className="text-xs text-muted-foreground">{visitor.company}</span>
              )}
            </Stack>
            {visitor.status === "EXPECTED" && (
              <Button
                size="sm"
                loading={checkIn.isPending}
                onClick={async () => {
                  try {
                    await checkIn.mutateAsync(visitor.id);
                    toast.success("Checked in");
                  } catch {
                    toast.error("Failed to check in");
                  }
                }}
              >
                Check In
              </Button>
            )}
            {visitor.status === "CHECKED_IN" && (
              <Button
                size="sm"
                variant="outline"
                loading={checkOut.isPending}
                onClick={async () => {
                  try {
                    await checkOut.mutateAsync(visitor.id);
                    toast.success("Checked out");
                  } catch {
                    toast.error("Failed to check out");
                  }
                }}
              >
                Check Out
              </Button>
            )}
          </Stack>
        </Card>
      ))}
    </Stack>
  );
}
