"use client";

import { useParams } from "next/navigation";
import { PageHeader, Stack, Card, Skeleton, EmptyState } from "@hostflow/ui";
import { useOfficePublicRooms } from "@hostflow/api-client/src/hooks/use-public-venue-directory";

export default function OfficeRoomsPage() {
  const { id } = useParams<{ id: string }>();
  const { data, isLoading, isError } = useOfficePublicRooms(id);

  return (
    <Stack gap="lg" className="p-6">
      <PageHeader title="Meeting Rooms" />

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load meeting rooms" description="Try refreshing." />
      )}
      {!isLoading && data && data.length === 0 && <EmptyState title="No meeting rooms listed yet" />}
      {!isLoading && data && data.length > 0 && (
        <Stack gap="sm">
          {data.map((room) => (
            <Card key={room.id} className="p-4 transition-shadow hover:shadow-md">
              <Stack direction="row" gap="sm" align="center">
                <span className="font-medium">{room.name}</span>
                <span className="text-sm text-muted-foreground">Capacity {room.capacity}</span>
              </Stack>
            </Card>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
