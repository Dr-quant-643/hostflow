"use client";

import { DataTable, Skeleton, EmptyState, Badge, Stack } from "@hostflow/ui";
import { useGuestSegments } from "@hostflow/api-client/src/hooks/use-analytics";
import type { GuestSegmentRow, GuestSegment } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";
import { formatKES } from "@/lib/currency";
import { GUEST_SEGMENT_VARIANT, GUEST_SEGMENT_LABEL } from "@/lib/status-badge";

const SEGMENT_ORDER: GuestSegment[] = ["VIP", "ACTIVE_TENANT", "REPEAT", "AT_RISK", "NEW"];

const columns: ColumnDef<GuestSegmentRow>[] = [
  { accessorKey: "name", header: "Guest", cell: ({ row }) => row.original.name ?? "Unknown guest" },
  {
    accessorKey: "segment",
    header: "Segment",
    cell: ({ row }) => (
      <Badge variant={GUEST_SEGMENT_VARIANT[row.original.segment]}>{GUEST_SEGMENT_LABEL[row.original.segment]}</Badge>
    ),
  },
  {
    id: "stays",
    header: "Stays",
    cell: ({ row }) => row.original.totalBookings + row.original.totalReservations,
  },
  {
    accessorKey: "totalSpend",
    header: "Total Spend",
    cell: ({ row }) => formatKES(row.original.totalSpend),
  },
  {
    accessorKey: "recencyDays",
    header: "Last Activity",
    cell: ({ row }) =>
      row.original.recencyDays == null ? "—" : row.original.recencyDays === 0 ? "Today" : `${row.original.recencyDays}d ago`,
  },
];

// Segment breakdown counts, shown above the table -- lets an owner see the
// shape of their guest base (how many VIPs, how many at risk) at a glance
// before scanning the individual rows.
function SegmentBreakdown({ rows }: { rows: GuestSegmentRow[] }) {
  return (
    <Stack direction="row" gap="sm" className="flex-wrap">
      {SEGMENT_ORDER.map((segment) => {
        const count = rows.filter((r) => r.segment === segment).length;
        if (count === 0) return null;
        return (
          <Badge key={segment} variant={GUEST_SEGMENT_VARIANT[segment]} className="text-sm">
            {GUEST_SEGMENT_LABEL[segment]}: {count}
          </Badge>
        );
      })}
    </Stack>
  );
}

export function GuestSegmentTable() {
  const { data, isLoading, isError } = useGuestSegments();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load guest segments" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No guests yet"
        description="Segments appear once guests have confirmed bookings or reservations."
      />
    );
  }

  return (
    <Stack gap="md">
      <SegmentBreakdown rows={data} />
      <DataTable columns={columns} data={data} />
    </Stack>
  );
}
