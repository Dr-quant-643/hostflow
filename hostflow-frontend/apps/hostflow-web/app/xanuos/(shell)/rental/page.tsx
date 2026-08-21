"use client";

import { useState } from "react";
import Link from "next/link";
import { PageHeader, Select, Stack, Skeleton, EmptyState, Button } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { LeaseForm } from "@/components/xanuos/rental/lease-form";
import { LeaseList } from "@/components/xanuos/rental/lease-list";

export default function RentalPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");

  const selected = propertyId || properties?.[0]?.id || "";

  return (
    <Stack gap="lg">
      <PageHeader
        title="Rental Management"
        description="Leases and tenants"
        actions={
          <Button asChild variant="outline">
            <Link href="/rental/tenants">Tenants</Link>
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
          <LeaseForm propertyId={selected} />
          <LeaseList propertyId={selected} />
        </>
      )}
    </Stack>
  );
}
