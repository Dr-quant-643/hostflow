"use client";

import { PageHeader, Card, Grid, Skeleton, EmptyState } from "@hostflow/ui";
import { usePlatformMonitoring } from "@hostflow/api-client/src/hooks/use-console-monitoring";
import type { MonitoringSnapshot } from "@hostflow/types";

const LABELS: Record<keyof MonitoringSnapshot, string> = {
  totalOrganizations: "Organizations",
  totalXanuosUsers: "XanuOS Users",
  totalGuestProfiles: "Guest Profiles",
  totalActiveBookings: "Active Bookings",
  totalOpenSupportTickets: "Open Support Tickets",
  totalOpenWorkOrders: "Open Work Orders",
};

export default function MonitoringPage() {
  const { data, isLoading, isError } = usePlatformMonitoring();

  return (
    <div>
      <PageHeader
        title="Monitoring"
        description="Cross-product usage counters (business metrics, not request/latency telemetry)"
      />

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load monitoring data" description="Try refreshing." />
      )}
      {!isLoading && data && (
        <Grid cols={3} gap="md">
          {(Object.keys(LABELS) as (keyof MonitoringSnapshot)[]).map((key) => (
            <Card key={key}>
              <p className="text-sm text-muted-foreground">{LABELS[key]}</p>
              <p className="text-2xl font-semibold">{data[key]}</p>
            </Card>
          ))}
        </Grid>
      )}
    </div>
  );
}
