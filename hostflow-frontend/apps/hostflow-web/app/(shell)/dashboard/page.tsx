"use client";

import { Stack, Grid, StatCard, PageHeader } from "@hostflow/ui";
import {
  usePropertyOccupancy,
  useMonthlyRevenue,
} from "@hostflow/api-client/src/hooks/use-analytics";
import { OccupancyChart } from "@/components/dashboard/occupancy-chart";

export default function DashboardPage() {
  const { data: occupancy } = usePropertyOccupancy();
  const { data: revenue } = useMonthlyRevenue();

  const totalBookings = occupancy?.reduce((sum, p) => sum + p.totalBookings, 0);
  const latestMonthRevenue = revenue?.[0]?.paidTotal;

  return (
    <Stack gap="lg">
      <PageHeader title="Dashboard" description="Portfolio overview" />

      <Grid cols={3} gap="md">
        <StatCard
          label="Active Properties"
          value={occupancy ? String(occupancy.length) : "—"}
        />
        <StatCard
          label="Total Bookings"
          value={totalBookings !== undefined ? String(totalBookings) : "—"}
        />
        <StatCard
          label="Revenue (Latest Month)"
          value={latestMonthRevenue ? `$${latestMonthRevenue}` : "—"}
        />
      </Grid>

      <OccupancyChart />
    </Stack>
  );
}
