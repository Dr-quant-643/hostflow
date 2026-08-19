"use client";

import Link from "next/link";
import { DataTable, Skeleton, EmptyState, Button, Badge } from "@hostflow/ui";
import { useCampaigns } from "@hostflow/api-client/src/hooks/use-marketing";
import type { Campaign } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<Campaign>[] = [
  { accessorKey: "name", header: "Campaign" },
  { accessorKey: "platform", header: "Platform" },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function CampaignList() {
  const { data, isLoading, isError } = useCampaigns();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState
        title="Couldn't load campaigns"
        description="Try refreshing."
      />
    );
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No campaigns yet"
        description="Create a campaign to start reaching leads."
        action={
          <Button asChild>
            <Link href="/marketing/new">New Campaign</Link>
          </Button>
        }
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data}
      onRowClick={(row) => {
        window.location.href = `/marketing/${row.id}`;
      }}
    />
  );
}
