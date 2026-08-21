"use client";

import { useState } from "react";
import Link from "next/link";
import { PageHeader, Select, Stack, Skeleton, EmptyState, Button } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { useMeetingRooms } from "@hostflow/api-client/src/hooks/use-office";
import { RoomForm } from "@/components/xanuos/office/room-form";
import { RoomList } from "@/components/xanuos/office/room-list";
import { RoomBookingForm } from "@/components/xanuos/office/room-booking-form";

export default function OfficePage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");
  const selected = propertyId || properties?.[0]?.id || "";
  const { data: rooms } = useMeetingRooms(selected);

  return (
    <Stack gap="lg">
      <PageHeader
        title="Office Management"
        description="Meeting rooms and visitors"
        actions={
          <Button asChild variant="outline">
            <Link href="/office/visitors">Visitors</Link>
          </Button>
        }
      />

      {isLoading && <Skeleton className="h-10 w-64" />}
      {!isLoading && (!properties || properties.length === 0) && (
        <EmptyState title="No properties yet" description="Add a property first." />
      )}
      {!isLoading && properties && properties.length > 0 && (
        <>
          <Select
            label="Property"
            value={selected}
            onChange={(e) => setPropertyId(e.target.value)}
            options={properties.map((p) => ({ value: p.id, label: p.name }))}
          />
          <RoomForm propertyId={selected} />
          <RoomList propertyId={selected} />
          {rooms && rooms.length > 0 && <RoomBookingForm rooms={rooms} />}
        </>
      )}
    </Stack>
  );
}
