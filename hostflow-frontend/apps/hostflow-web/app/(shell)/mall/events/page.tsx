"use client";

import { useState } from "react";
import { PageHeader, Select, Stack, Skeleton, EmptyState } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { MallEventForm } from "@/components/mall/mall-event-form";
import { MallEventList } from "@/components/mall/mall-event-list";

export default function MallEventsPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");
  const selected = propertyId || properties?.[0]?.id || "";

  return (
    <Stack gap="lg">
      <PageHeader title="Mall Events" description="Publicly readable events for guests" />

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
          <MallEventForm propertyId={selected} />
          <MallEventList propertyId={selected} />
        </>
      )}
    </Stack>
  );
}
