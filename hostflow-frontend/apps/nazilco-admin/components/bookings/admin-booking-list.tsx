"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useAllGuestBookings } from "@hostflow/api-client/src/hooks/use-nazilco-admin-bookings";
import type { BookingOversightRow } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<BookingOversightRow>[] = [
  { accessorKey: "organizationName", header: "Organization" },
  { accessorKey: "checkIn", header: "Check In" },
  { accessorKey: "checkOut", header: "Check Out" },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function AdminBookingList() {
  const { data, isLoading, isError } = useAllGuestBookings();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load cross-tenant bookings" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No bookings found" />;
  }

  return <DataTable columns={columns} data={data} />;
}
