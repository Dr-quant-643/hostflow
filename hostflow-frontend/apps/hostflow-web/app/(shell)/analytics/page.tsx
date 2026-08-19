import { PageHeader, Stack } from "@hostflow/ui";
import { RevenueTrendChart } from "@/components/analytics/revenue-trend-chart";
import { OccupancyByProperty } from "@/components/analytics/occupancy-by-property";

export default function AnalyticsPage() {
  return (
    <Stack gap="lg">
      <PageHeader
        title="Analytics"
        description="Portfolio performance over time"
      />
      <RevenueTrendChart />
      <OccupancyByProperty />
    </Stack>
  );
}
