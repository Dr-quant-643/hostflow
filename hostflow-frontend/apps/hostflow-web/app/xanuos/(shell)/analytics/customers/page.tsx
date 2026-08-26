import { PageHeader, Stack } from "@hostflow/ui";
import { GuestSegmentTable } from "@/components/xanuos/analytics/guest-segment-table";
import { SegmentCampaignPanel } from "@/components/xanuos/analytics/segment-campaign-panel";

export default function CustomerSegmentsPage() {
  return (
    <Stack gap="lg">
      <PageHeader
        title="Customer Segments"
        description="Guests and tenants across your properties, grouped by how they engage"
      />
      <GuestSegmentTable />
      <Stack gap="sm">
        <h2 className="text-sm font-semibold uppercase tracking-wide text-muted-foreground">Campaigns</h2>
        <SegmentCampaignPanel />
      </Stack>
    </Stack>
  );
}
