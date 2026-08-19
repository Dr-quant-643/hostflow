"use client";

import { useState } from "react";
import Link from "next/link";
import { PageHeader, Select, Stack, Skeleton, EmptyState, Button } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { WorkOrderForm } from "@/components/maintenance/work-order-form";
import { WorkOrderList } from "@/components/maintenance/work-order-list";

export default function MaintenancePage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");

  const selected = propertyId || properties?.[0]?.id || "";

  return (
    <Stack gap="lg">
      <PageHeader
        title="Maintenance"
        description="Report and track property maintenance issues"
        actions={
          <Stack direction="row" gap="sm">
            <Button asChild variant="outline">
              <Link href="/maintenance/my-assignments">My Assignments</Link>
            </Button>
            <Button asChild variant="outline">
              <Link href="/maintenance/assets">Assets</Link>
            </Button>
          </Stack>
        }
      />

      {isLoading && <Skeleton className="h-10 w-64" />}
      {!isLoading && (!properties || properties.length === 0) && (
        <EmptyState
          title="No properties yet"
          description="Add a property first before reporting maintenance issues."
        />
      )}
      {!isLoading && properties && properties.length > 0 && (
        <>
          <Select
            label="Property"
            value={selected}
            onChange={(e) => setPropertyId(e.target.value)}
            options={properties.map((p) => ({ value: p.id, label: p.name }))}
          />
          <WorkOrderForm propertyId={selected} />
          <WorkOrderList propertyId={selected} />
        </>
      )}
    </Stack>
  );
}
