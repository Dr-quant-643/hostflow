import { PageHeader, Card, Stack } from "@hostflow/ui";
import { ApiKeyPanel } from "@/components/xanuos/settings/api-key-panel";
import { WebhookPanel } from "@/components/xanuos/settings/webhook-panel";

export default function DeveloperSettingsPage() {
  return (
    <Stack gap="lg">
      <PageHeader
        title="Developer"
        description="API keys and webhooks for reading your data programmatically"
      />
      <Card className="p-6">
        <Stack gap="md">
          <h3 className="font-medium">API Keys</h3>
          <p className="text-sm text-muted-foreground">
            Use a key in the <code>X-Api-Key</code> header to read your properties, bookings, and guest segments.
          </p>
          <ApiKeyPanel />
        </Stack>
      </Card>
      <Card className="p-6">
        <Stack gap="md">
          <h3 className="font-medium">Webhooks</h3>
          <p className="text-sm text-muted-foreground">Get notified the moment a booking is created or confirmed.</p>
          <WebhookPanel />
        </Stack>
      </Card>
    </Stack>
  );
}
