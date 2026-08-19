"use client";

import { BarChartWidget, Skeleton, EmptyState } from "@hostflow/ui";
import { usePropertyOccupancy } from "@hostflow/api-client/src/hooks/use-analytics";

export function OccupancyChart() {
  const { data, isLoading, isError } = usePropertyOccupancy();

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError || !data || data.length === 0) {
    return (
      <EmptyState
        title="No occupancy data yet"
        description="Occupancy will populate once bookings exist for your properties."
      />
    );
  }

  return (
    <BarChartWidget
      data={data.map((p) => ({
        propertyName: p.propertyName,
        totalNightsBooked: p.totalNightsBooked,
      }))}
      xKey="propertyName"
      series={[{ key: "totalNightsBooked", color: "#2563EB", label: "Nights Booked" }]}
    />
  );
}
