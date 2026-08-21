"use client";

import { DataTable, Skeleton, EmptyState, Badge, Button, toast } from "@hostflow/ui";
import { useAssets, useDecommissionAsset } from "@hostflow/api-client/src/hooks/use-maintenance";
import type { AssetResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

export function AssetList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useAssets(propertyId);
  const decommission = useDecommissionAsset(propertyId);

  const columns: ColumnDef<AssetResponse>[] = [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "category", header: "Category" },
    { accessorKey: "serialNumber", header: "Serial #" },
    {
      accessorKey: "underWarranty",
      header: "Warranty",
      cell: ({ row }) => (
        <Badge variant={row.original.underWarranty ? "default" : "outline"}>
          {row.original.underWarranty ? "Under Warranty" : "Expired"}
        </Badge>
      ),
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => (
        <Button
          size="sm"
          variant="ghost"
          loading={decommission.isPending}
          onClick={async () => {
            try {
              await decommission.mutateAsync(row.original.id);
              toast.success("Asset decommissioned");
            } catch {
              toast.error("Failed to decommission asset");
            }
          }}
        >
          Decommission
        </Button>
      ),
    },
  ];

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load assets" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No assets recorded yet" />;
  }

  return <DataTable columns={columns} data={data} />;
}
