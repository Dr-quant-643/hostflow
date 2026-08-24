"use client";

import { useState } from "react";
import { Stack, Input, Button, Badge, toast } from "@hostflow/ui";
import { useSetPropertyOccupancy, useClearPropertyOccupancy } from "@hostflow/api-client/src/hooks/use-properties";
import type { PropertyResponse } from "@hostflow/types";

// Independent of Booking/Lease data -- for cases those don't know about (a
// walk-in meeting in an office hall, maintenance, etc). Shown on NazilCo as
// "In use until X" so other guests can plan around it, without unpublishing
// the property. Applies to any rental model/property status.
export function OccupancyControl({ property }: { property: PropertyResponse }) {
  const [until, setUntil] = useState("");
  const setOccupancy = useSetPropertyOccupancy(property.id);
  const clearOccupancy = useClearPropertyOccupancy(property.id);

  const occupiedUntil = property.manualOccupiedUntil ? new Date(property.manualOccupiedUntil) : null;
  const isCurrentlyMarked = occupiedUntil != null && occupiedUntil.getTime() > Date.now();

  const onMarkOccupied = async () => {
    if (!until) return;
    try {
      await setOccupancy.mutateAsync(new Date(until).toISOString());
      toast.success("Property marked occupied");
    } catch {
      toast.error("Couldn't mark property occupied");
    }
  };

  const onClear = async () => {
    try {
      await clearOccupancy.mutateAsync();
      toast.success("Occupancy cleared");
    } catch {
      toast.error("Couldn't clear occupancy");
    }
  };

  return (
    <Stack gap="md">
      <p className="text-sm text-muted-foreground">
        Mark this property as in use right now for reasons NazilCo's booking data doesn't know
        about (e.g. a walk-in meeting or viewing) — guests will see when it frees up without the
        listing being unpublished.
      </p>
      {isCurrentlyMarked && occupiedUntil ? (
        <Stack direction="row" gap="sm" align="center">
          <Badge variant="warning">
            Occupied until{" "}
            {occupiedUntil.toLocaleString(undefined, {
              month: "short",
              day: "numeric",
              hour: "numeric",
              minute: "2-digit",
            })}
          </Badge>
          <Button variant="outline" loading={clearOccupancy.isPending} onClick={onClear}>
            Clear
          </Button>
        </Stack>
      ) : (
        <Stack direction="row" gap="sm" align="center">
          <Input
            type="datetime-local"
            value={until}
            onChange={(e) => setUntil(e.target.value)}
            className="max-w-xs"
          />
          <Button disabled={!until} loading={setOccupancy.isPending} onClick={onMarkOccupied}>
            Mark Occupied
          </Button>
        </Stack>
      )}
    </Stack>
  );
}
