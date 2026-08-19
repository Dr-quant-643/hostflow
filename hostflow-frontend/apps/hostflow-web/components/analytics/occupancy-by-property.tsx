"use client";

import { DataTable, Skeleton, EmptyState, Card } from "@hostflow/ui";
import { usePropertyOccupancy } from "@hostflow/api-client/src/hooks/use-analytics";
import type { PropertyOccupancyResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<PropertyOccupancyResponse>[] = [
  { accessorKey: "propertyName", header: "Property" },
  { accessorKey: "totalBookings", header: "Bookings" },
  { accessorKey: "totalNightsBooked", header: "Nights Booked" },
  {
    accessorKey: "totalRevenue",
    header: "Revenue",
    cell: ({ row }) => `$${row.original.totalRevenue}`,
  },
];

export function OccupancyByProperty() {
  const { data, isLoading, isError } = usePropertyOccupancy();

  return (
    <Card>
      <h3 className="mb-3 font-medium">Occupancy by Property</h3>
      {isLoading && <Skeleton className="h-48 w-full" />}
      {!isLoading && (isError || !data || data.length === 0) && (
        <EmptyState title="No properties with occupancy data yet" />
      )}
      {!isLoading && data && data.length > 0 && (
        <DataTable columns={columns} data={data} />
      )}
    </Card>
  );
}
