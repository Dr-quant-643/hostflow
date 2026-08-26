import { PageHeader, Stack } from "@hostflow/ui";
import { GuestSegmentTable } from "@/components/xanuos/analytics/guest-segment-table";

export default function CustomerSegmentsPage() {
  return (
    <Stack gap="lg">
      <PageHeader
        title="Customer Segments"
        description="Guests and tenants across your properties, grouped by how they engage"
      />
      <GuestSegmentTable />
    </Stack>
  );
}
