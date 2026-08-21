"use client";

import { Stack, Card, Skeleton, EmptyState } from "@hostflow/ui";
import { useMallEvents } from "@hostflow/api-client/src/hooks/use-mall";

export function MallEventList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useMallEvents(propertyId);

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load events" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No events scheduled yet" />;
  }

  return (
    <Stack gap="sm">
      {data.map((event) => (
        <Card key={event.id}>
          <Stack gap="sm">
            <p className="font-medium">{event.title}</p>
            <p className="text-xs text-muted-foreground">
              {event.startsAt} → {event.endsAt}
            </p>
            {event.description && <p className="text-sm">{event.description}</p>}
          </Stack>
        </Card>
      ))}
    </Stack>
  );
}
