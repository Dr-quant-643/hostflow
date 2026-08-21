"use client";

import { useState } from "react";
import { PageHeader, Select, Stack, Skeleton, EmptyState } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { VisitorForm } from "@/components/xanuos/office/visitor-form";
import { VisitorList } from "@/components/xanuos/office/visitor-list";

export default function VisitorsPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");
  const selected = propertyId || properties?.[0]?.id || "";

  return (
    <Stack gap="lg">
      <PageHeader title="Visitors" description="Track expected and checked-in visitors" />

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
          <VisitorForm propertyId={selected} />
          <VisitorList propertyId={selected} />
        </>
      )}
    </Stack>
  );
}
