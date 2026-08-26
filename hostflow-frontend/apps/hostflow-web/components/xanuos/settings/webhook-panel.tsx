"use client";

import { useState } from "react";
import { Stack, Input, Select, Button, Badge, EmptyState, Skeleton, toast } from "@hostflow/ui";
import {
  useWebhookSubscriptions,
  useCreateWebhookSubscription,
  useDeactivateWebhookSubscription,
} from "@hostflow/api-client/src/hooks/use-developer";

const EVENT_OPTIONS = [
  { value: "booking.created", label: "Booking created" },
  { value: "booking.confirmed", label: "Booking confirmed" },
];

export function WebhookPanel() {
  const { data, isLoading } = useWebhookSubscriptions();
  const create = useCreateWebhookSubscription();
  const deactivate = useDeactivateWebhookSubscription();
  const [url, setUrl] = useState("");
  const [eventType, setEventType] = useState(EVENT_OPTIONS[0]!.value);

  const onCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!url.trim()) return;
    try {
      await create.mutateAsync({ url, eventType });
      setUrl("");
      toast.success("Webhook connected");
    } catch {
      toast.error("Couldn't connect the webhook");
    }
  };

  return (
    <Stack gap="md">
      <form onSubmit={onCreate}>
        <Stack direction="row" gap="sm">
          <Select value={eventType} onChange={(e) => setEventType(e.target.value)} options={EVENT_OPTIONS} className="max-w-[220px]" />
          <Input placeholder="https://your-server.com/webhooks/rvanaflow" value={url} onChange={(e) => setUrl(e.target.value)} className="flex-1" />
          <Button type="submit" disabled={!url.trim()} loading={create.isPending}>
            Connect
          </Button>
        </Stack>
      </form>
      <p className="text-xs text-muted-foreground">
        Each delivery is signed with a per-webhook secret (HMAC-SHA256, header <code>X-RvanaFlow-Signature</code>) so
        your endpoint can verify it really came from RvanaFlow.
      </p>

      {isLoading ? (
        <Skeleton className="h-24 w-full" />
      ) : !data || data.length === 0 ? (
        <EmptyState title="No webhooks yet" description="Connect one above to get notified in real time." />
      ) : (
        <Stack gap="sm">
          {data.map((webhook) => (
            <div key={webhook.id} className="flex items-center justify-between rounded-lg border border-border p-3">
              <Stack gap="xs">
                <Stack direction="row" gap="sm" align="center">
                  <Badge variant="outline">{webhook.eventType}</Badge>
                  {!webhook.active && <Badge variant="destructive">Inactive</Badge>}
                </Stack>
                <p className="max-w-md truncate text-xs text-muted-foreground">{webhook.url}</p>
              </Stack>
              {webhook.active && (
                <Button
                  size="sm"
                  variant="outline"
                  loading={deactivate.isPending}
                  onClick={async () => {
                    try {
                      await deactivate.mutateAsync(webhook.id);
                      toast.success("Webhook disconnected");
                    } catch {
                      toast.error("Couldn't disconnect");
                    }
                  }}
                >
                  Disconnect
                </Button>
              )}
            </div>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
