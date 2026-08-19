"use client";

import { useState } from "react";
import { PageHeader, Select, Stack, Skeleton, EmptyState, Card } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { AssetForm } from "@/components/maintenance/asset-form";
import { AssetList } from "@/components/maintenance/asset-list";
import { MaintenanceScheduleForm } from "@/components/maintenance/maintenance-schedule-form";

export default function AssetsPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");

  const selected = propertyId || properties?.[0]?.id || "";

  return (
    <Stack gap="lg">
      <PageHeader title="Assets" description="Track equipment per property" />

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
          <AssetForm propertyId={selected} />
          <AssetList propertyId={selected} />

          <Card>
            <Stack gap="md">
              <h3 className="font-medium">Preventive Maintenance Schedule</h3>
              <MaintenanceScheduleForm propertyId={selected} />
            </Stack>
          </Card>
        </>
      )}
    </Stack>
  );
}
