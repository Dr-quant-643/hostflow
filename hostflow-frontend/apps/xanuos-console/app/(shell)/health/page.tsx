"use client";

import { PageHeader, Card, Stack, Badge, Skeleton, EmptyState } from "@hostflow/ui";
import { useSystemHealth } from "@hostflow/api-client/src/hooks/use-console-health";

export default function SystemHealthPage() {
  const { data, isLoading, isError } = useSystemHealth();

  return (
    <div>
      <PageHeader
        title="System Health"
        description="Service status across the platform"
        actions={data && <Badge variant={data.status === "UP" ? "success" : "warning"}>{data.status}</Badge>}
      />

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't reach the health endpoint" description="The gateway may be down." />
      )}
      {!isLoading && data && (
        <Stack gap="sm">
          {Object.entries(data.components).map(([name, component]) => (
            <Card key={name}>
              <Stack direction="row" justify="between" align="center">
                <Stack gap="sm">
                  <span className="font-medium capitalize">{name}</span>
                  <span className="text-sm text-muted-foreground">{component.detail}</span>
                </Stack>
                <Badge variant={component.status === "UP" ? "success" : "warning"}>
                  {component.status}
                </Badge>
              </Stack>
            </Card>
          ))}
        </Stack>
      )}
    </div>
  );
}
