"use client";

import Link from "next/link";
import { DataTable, Skeleton, EmptyState, Button, Badge } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import type { PropertyResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";
import { formatKES } from "@/lib/currency";

const STATUS_BADGE: Record<string, { variant: "success" | "warning" | "secondary" | "destructive"; label: string }> = {
  ACTIVE: { variant: "success", label: "Live on NazilCo" },
  PUBLISHED: { variant: "success", label: "Live on NazilCo" },
  DRAFT: { variant: "warning", label: "Draft" },
  INACTIVE: { variant: "secondary", label: "Unpublished" },
  ARCHIVED: { variant: "destructive", label: "Archived" },
};

const columns: ColumnDef<PropertyResponse>[] = [
  { accessorKey: "name", header: "Name" },
  { accessorKey: "city", header: "City" },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => {
      const meta = STATUS_BADGE[row.original.status] ?? { variant: "secondary" as const, label: row.original.status };
      return <Badge variant={meta.variant}>{meta.label}</Badge>;
    },
  },
  {
    accessorKey: "basePrice",
    header: "Nightly Rate",
    cell: ({ row }) => (row.original.basePrice ? formatKES(row.original.basePrice) : "—"),
  },
  {
    accessorKey: "occupancyPercent",
    header: "Occupancy",
    cell: ({ row }) => {
      const { totalUnits, occupiedUnits, occupancyPercent } = row.original;
      if (totalUnits <= 1) return "—";
      return `${occupiedUnits}/${totalUnits} (${occupancyPercent}%)`;
    },
  },
];

export function PropertyList() {
  const { data, isLoading, isError } = useProperties();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState
        title="Couldn't load properties"
        description="Try refreshing."
      />
    );
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No properties yet"
        description="Add your first property to get started."
        action={
          <Button asChild>
            <Link href="/xanuos/properties/new">Add Property</Link>
          </Button>
        }
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data}
      onRowClick={(row) => {
        window.location.href = `/xanuos/properties/${row.id}`;
      }}
    />
  );
}
