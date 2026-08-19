"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useRetailUnits } from "@hostflow/api-client/src/hooks/use-mall";
import type { RetailUnitResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<RetailUnitResponse>[] = [
  { accessorKey: "unitNumber", header: "Unit" },
  {
    accessorKey: "sizeSqm",
    header: "Size",
    cell: ({ row }) => (row.original.sizeSqm ? `${row.original.sizeSqm} sqm` : "—"),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function RetailUnitList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useRetailUnits(propertyId);

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load retail units" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No retail units yet" description="Add a unit using the form above." />;
  }

  return <DataTable columns={columns} data={data} />;
}
