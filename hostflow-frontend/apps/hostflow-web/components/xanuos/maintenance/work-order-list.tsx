"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useWorkOrders } from "@hostflow/api-client/src/hooks/use-maintenance";
import type { WorkOrderResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<WorkOrderResponse>[] = [
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

export function WorkOrderList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useWorkOrders(propertyId);

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load work orders" description="Try refreshing." />;
  }
  if (!data || data.content.length === 0) {
    return (
      <EmptyState
        title="No work orders yet"
        description="Report an issue for this property using the form above."
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data.content}
      onRowClick={(row) => {
        window.location.href = `/xanuos/maintenance/${row.id}`;
      }}
    />
  );
}
