"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useMeetingRooms } from "@hostflow/api-client/src/hooks/use-office";
import type { MeetingRoomResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<MeetingRoomResponse>[] = [
  { accessorKey: "name", header: "Room" },
  { accessorKey: "capacity", header: "Capacity" },
  {
    accessorKey: "active",
    header: "Status",
    cell: ({ row }) => <Badge variant={row.original.active ? "default" : "outline"}>
      {row.original.active ? "Active" : "Inactive"}
    </Badge>,
  },
];

export function RoomList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useMeetingRooms(propertyId);

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load rooms" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No meeting rooms yet" description="Add a room using the form above." />;
  }

  return <DataTable columns={columns} data={data} />;
}
