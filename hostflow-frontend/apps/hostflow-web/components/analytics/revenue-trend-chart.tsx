"use client";

import { LineChartWidget, Skeleton, EmptyState, Card } from "@hostflow/ui";
import { useMonthlyRevenue } from "@hostflow/api-client/src/hooks/use-analytics";

export function RevenueTrendChart() {
  const { data, isLoading, isError } = useMonthlyRevenue();

  return (
    <Card>
      <h3 className="mb-3 font-medium">Monthly Revenue</h3>
      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && (isError || !data || data.length === 0) && (
        <EmptyState title="No revenue history yet" />
      )}
      {!isLoading && data && data.length > 0 && (
        <LineChartWidget
          data={[...data]
            .reverse()
            .map((m) => ({ month: m.month, paidTotal: Number(m.paidTotal) }))}
          xKey="month"
          series={[{ key: "paidTotal", color: "#2563EB", label: "Paid" }]}
        />
      )}
    </Card>
  );
}
