"use client";

import { useParams } from "next/navigation";
import { PageHeader, Stack, Card, Skeleton, EmptyState } from "@hostflow/ui";
import { useMallStoreDirectory } from "@hostflow/api-client/src/hooks/use-public-venue-directory";

export default function MallDirectoryPage() {
  const { id } = useParams<{ id: string }>();
  const { data, isLoading, isError } = useMallStoreDirectory(id);

  return (
    <Stack gap="lg" className="p-6">
      <PageHeader title="Store Directory" />

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load the store directory" description="Try refreshing." />
      )}
      {!isLoading && data && data.length === 0 && <EmptyState title="No stores listed yet" />}
      {!isLoading && data && data.length > 0 && (
        <Stack gap="sm">
          {data.map((store) => (
            <Card key={store.unitNumber} className="p-4 transition-shadow hover:shadow-md">
              <Stack direction="row" gap="sm" align="center">
                <span className="font-medium">{store.businessName}</span>
                <span className="text-sm text-muted-foreground">Unit {store.unitNumber}</span>
              </Stack>
            </Card>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
