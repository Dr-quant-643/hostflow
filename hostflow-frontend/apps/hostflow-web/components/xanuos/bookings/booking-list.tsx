"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useBookings } from "@hostflow/api-client/src/hooks/use-bookings";
import type { BookingResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<BookingResponse>[] = [
  { accessorKey: "propertyId", header: "Property" },
  { accessorKey: "checkIn", header: "Check In" },
  { accessorKey: "checkOut", header: "Check Out" },
  {
    accessorKey: "totalPrice",
    header: "Total",
    cell: ({ row }) => `$${row.original.totalPrice}`,
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function BookingList() {
  const { data, isLoading, isError } = useBookings();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState
        title="Couldn't load bookings"
        description="Try refreshing."
      />
    );
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No bookings yet"
        description="Bookings will appear here once guests book a stay."
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data}
      onRowClick={(row) => {
        window.location.href = `/xanuos/bookings/${row.id}`;
      }}
    />
  );
}
