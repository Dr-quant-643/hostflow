"use client";

import { PageHeader, Card, Stack, Badge, Skeleton, EmptyState } from "@hostflow/ui";
import { useAuditLog } from "@hostflow/api-client/src/hooks/use-console-audit-log";

export default function AuditLogPage() {
  const { data, isLoading, isError } = useAuditLog();

  return (
    <Stack gap="lg">
      <PageHeader title="Audit Log" description="Platform-wide activity log" />

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load the audit log" description="Try refreshing." />
      )}
      {!isLoading && data && data.content.length === 0 && (
        <EmptyState title="No audit events yet" />
      )}
      {!isLoading && data && data.content.length > 0 && (
        <Stack gap="sm">
          {data.content.map((entry) => (
            <Card key={entry.id}>
              <Stack direction="row" justify="between" align="center">
                <Stack gap="sm">
                  <Stack direction="row" gap="sm" align="center">
                    <Badge variant="outline">{entry.action}</Badge>
                    <span className="text-sm">
                      {entry.resourceType} · {entry.resourceId}
                    </span>
                  </Stack>
                  {entry.detail && (
                    <span className="text-sm text-muted-foreground">{entry.detail}</span>
                  )}
                </Stack>
                <span className="text-sm text-muted-foreground">
                  {new Date(entry.createdAt).toLocaleString()}
                </span>
              </Stack>
            </Card>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
