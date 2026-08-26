import Link from "next/link";
import { PageHeader, Stack, Button } from "@hostflow/ui";
import { RevenueTrendChart } from "@/components/xanuos/analytics/revenue-trend-chart";
import { OccupancyByProperty } from "@/components/xanuos/analytics/occupancy-by-property";

export default function AnalyticsPage() {
  return (
    <Stack gap="lg">
      <PageHeader
        title="Analytics"
        description="Portfolio performance over time"
        actions={
          <Button asChild variant="outline">
            <Link href="/xanuos/analytics/customers">Customer Segments</Link>
          </Button>
        }
      />
      <RevenueTrendChart />
      <OccupancyByProperty />
    </Stack>
  );
}
