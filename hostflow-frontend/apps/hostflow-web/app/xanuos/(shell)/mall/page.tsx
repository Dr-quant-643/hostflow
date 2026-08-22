"use client";

import { useState } from "react";
import Link from "next/link";
import { PageHeader, Select, Stack, Skeleton, EmptyState, Button } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { useRetailUnits } from "@hostflow/api-client/src/hooks/use-mall";
import { RetailUnitForm } from "@/components/xanuos/mall/retail-unit-form";
import { RetailUnitList } from "@/components/xanuos/mall/retail-unit-list";
import { AssignTenantForm } from "@/components/xanuos/mall/assign-tenant-form";

export default function MallPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");
  const selected = propertyId || properties?.[0]?.id || "";
  const { data: units } = useRetailUnits(selected);

  return (
    <Stack gap="lg">
      <PageHeader
        title="Mall Management"
        description="Retail units, events, and parking"
        actions={
          <Stack direction="row" gap="sm">
            <Button asChild variant="outline">
              <Link href="/xanuos/mall/events">Events</Link>
            </Button>
            <Button asChild variant="outline">
              <Link href="/xanuos/mall/parking">Parking</Link>
            </Button>
          </Stack>
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
          <RetailUnitForm propertyId={selected} />
          <RetailUnitList propertyId={selected} />
          {units && units.length > 0 && <AssignTenantForm propertyId={selected} units={units} />}
        </>
      )}
    </Stack>
  );
}
