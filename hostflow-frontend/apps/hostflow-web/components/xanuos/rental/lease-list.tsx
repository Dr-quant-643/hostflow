"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useLeases } from "@hostflow/api-client/src/hooks/use-rental";
import type { LeaseResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";
import { formatKES } from "@/lib/currency";

const columns: ColumnDef<LeaseResponse>[] = [
  { accessorKey: "startDate", header: "Start" },
  { accessorKey: "endDate", header: "End" },
  {
    accessorKey: "monthlyRent",
    header: "Monthly Rent",
    cell: ({ row }) => formatKES(row.original.monthlyRent),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function LeaseList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useLeases(propertyId);

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load leases" description="Try refreshing." />;
  }
  if (!data || data.content.length === 0) {
    return (
      <EmptyState
        title="No leases yet"
        description="Create a lease for this property using the form above."
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data.content}
      onRowClick={(row) => {
        window.location.href = `/xanuos/rental/${row.id}`;
      }}
    />
  );
}
