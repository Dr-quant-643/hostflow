import { PageHeader, Stack } from "@hostflow/ui";
import { PricingSuggestions } from "@/components/xanuos/analytics/pricing-suggestions";

export default function PricingSuggestionsPage() {
  return (
    <Stack gap="lg">
      <PageHeader
        title="Pricing Suggestions"
        description="Demand-based price suggestions for your nightly-rate properties, based on how fast they're currently booking up"
      />
      <PricingSuggestions />
    </Stack>
  );
}
