"use client";

import { useState } from "react";
import { Stack, Input, Button, Badge, EmptyState, Skeleton, toast } from "@hostflow/ui";
import {
  useExternalCalendarLinks,
  useCreateExternalCalendarLink,
  useDeleteExternalCalendarLink,
} from "@hostflow/api-client/src/hooks/use-external-calendars";

// The API gateway's public base URL -- same env var apps/hostflow-web already
// declares (NEXT_PUBLIC_API_BASE_URL) for cases where a URL needs to be
// reachable directly by a third party (here: Airbnb/Booking.com/VRBO's
// servers polling this property's own calendar.ics feed), not proxied
// through this app's authenticated session-bound API routes.
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";

function ExportUrlBox({ propertyId }: { propertyId: string }) {
  const exportUrl = `${API_BASE_URL}/properties/public/${propertyId}/calendar.ics`;
  return (
    <Stack gap="xs">
      <p className="text-sm font-medium">Export this property&apos;s calendar</p>
      <p className="text-xs text-muted-foreground">
        Paste this URL into Airbnb, Booking.com, or VRBO&apos;s &ldquo;import calendar&rdquo; field so they know
        which dates are already booked here.
      </p>
      <div className="flex items-center gap-2">
        <Input readOnly value={exportUrl} className="font-mono text-xs" onFocus={(e) => e.target.select()} />
        <Button
          size="sm"
          variant="outline"
          onClick={() => {
            navigator.clipboard.writeText(exportUrl);
            toast.success("Copied");
          }}
        >
          Copy
        </Button>
      </div>
    </Stack>
  );
}

function AddLinkForm({ propertyId }: { propertyId: string }) {
  const [icsUrl, setIcsUrl] = useState("");
  const [label, setLabel] = useState("");
  const create = useCreateExternalCalendarLink();

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!icsUrl.trim()) return;
    try {
      await create.mutateAsync({ propertyId, icsUrl, label: label || undefined });
      setIcsUrl("");
      setLabel("");
      toast.success("Calendar connected — dates sync every few hours");
    } catch {
      toast.error("Couldn't connect that calendar. Check the URL and try again.");
    }
  };

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="sm">
        <p className="text-sm font-medium">Import a calendar from another platform</p>
        <Stack direction="row" gap="sm">
          <Input
            placeholder="e.g. Airbnb"
            value={label}
            onChange={(e) => setLabel(e.target.value)}
            className="max-w-[160px]"
          />
          <Input
            placeholder="https://www.airbnb.com/calendar/ical/....ics"
            value={icsUrl}
            onChange={(e) => setIcsUrl(e.target.value)}
            className="flex-1"
          />
          <Button type="submit" disabled={!icsUrl.trim()} loading={create.isPending}>
            Connect
          </Button>
        </Stack>
      </Stack>
    </form>
  );
}

function LinkedCalendarList({ propertyId }: { propertyId: string }) {
  const { data, isLoading } = useExternalCalendarLinks(propertyId);
  const del = useDeleteExternalCalendarLink(propertyId);

  if (isLoading) return <Skeleton className="h-16 w-full" />;
  if (!data || data.length === 0) {
    return <EmptyState title="No calendars connected yet" description="Connect one above to prevent double-bookings." />;
  }

  return (
    <Stack gap="sm">
      {data.map((link) => (
        <div key={link.id} className="flex items-center justify-between rounded-lg border border-border p-3">
          <Stack gap="xs">
            <p className="text-sm font-medium">{link.label || "Connected calendar"}</p>
            <p className="max-w-md truncate text-xs text-muted-foreground">{link.icsUrl}</p>
            {link.lastSyncError ? (
              <Badge variant="destructive">Sync failed: {link.lastSyncError}</Badge>
            ) : link.lastSyncedAt ? (
              <span className="text-xs text-muted-foreground">Last synced {new Date(link.lastSyncedAt).toLocaleString()}</span>
            ) : (
              <span className="text-xs text-muted-foreground">Not synced yet</span>
            )}
          </Stack>
          <Button
            size="sm"
            variant="outline"
            loading={del.isPending}
            onClick={async () => {
              try {
                await del.mutateAsync(link.id);
                toast.success("Calendar disconnected");
              } catch {
                toast.error("Couldn't disconnect");
              }
            }}
          >
            Disconnect
          </Button>
        </div>
      ))}
    </Stack>
  );
}

export function ExternalCalendarPanel({ propertyId }: { propertyId: string }) {
  return (
    <Stack gap="lg">
      <ExportUrlBox propertyId={propertyId} />
      <AddLinkForm propertyId={propertyId} />
      <LinkedCalendarList propertyId={propertyId} />
    </Stack>
  );
}
