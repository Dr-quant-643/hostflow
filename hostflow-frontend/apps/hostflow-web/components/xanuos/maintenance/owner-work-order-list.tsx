"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useOwnerWorkOrders } from "@hostflow/api-client/src/hooks/use-maintenance";
import type { OwnerWorkOrderRow } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<OwnerWorkOrderRow>[] = [
  { accessorKey: "propertyName", header: "Property" },
  { accessorKey: "title", header: "Title" },
  { accessorKey: "category", header: "Category" },
  {
    accessorKey: "priority",
    header: "Priority",
    cell: ({ row }) => <Badge>{row.original.priority}</Badge>,
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

// The default, cross-property view -- a tenant-reported issue (or a work
// order on any property) shows up here without the owner first having to
// pick the right property from the dropdown below.
export function OwnerWorkOrderList() {
  const { data, isLoading, isError } = useOwnerWorkOrders();

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load work orders" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No maintenance issues yet"
        description="Issues reported by tenants, or logged by your team, will appear here."
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data}
      onRowClick={(row) => {
        window.location.href = `/xanuos/maintenance/${row.id}`;
      }}
    />
  );
}
