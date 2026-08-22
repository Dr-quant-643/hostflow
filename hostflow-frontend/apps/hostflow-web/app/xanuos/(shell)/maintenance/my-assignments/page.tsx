"use client";

import { PageHeader, DataTable, Skeleton, EmptyState, Badge, Stack } from "@hostflow/ui";
import { useMyAssignments } from "@hostflow/api-client/src/hooks/use-maintenance";
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

export default function MyAssignmentsPage() {
  const { data, isLoading, isError } = useMyAssignments();

  return (
    <Stack gap="lg">
      <PageHeader title="My Assignments" description="Work orders assigned to you" />
      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load assignments" description="Try refreshing." />
      )}
      {!isLoading && data && data.content.length === 0 && (
        <EmptyState title="No assignments yet" />
      )}
      {!isLoading && data && data.content.length > 0 && (
        <DataTable
          columns={columns}
          data={data.content}
          onRowClick={(row) => {
            window.location.href = `/xanuos/maintenance/${row.id}`;
          }}
        />
      )}
    </Stack>
  );
}
