"use client";

import { useState } from "react";
import { PageHeader, Select, Input, Stack, Skeleton, EmptyState, Button, Card, toast } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { useParkingEntry, useParkingExit } from "@hostflow/api-client/src/hooks/use-mall";
import type { ParkingSessionResponse } from "@hostflow/types";

export default function ParkingPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");
  const selected = propertyId || properties?.[0]?.id || "";

  const [plate, setPlate] = useState("");
  const [exitId, setExitId] = useState("");
  const [lastSession, setLastSession] = useState<ParkingSessionResponse | null>(null);
  const [exitedSession, setExitedSession] = useState<ParkingSessionResponse | null>(null);

  const entry = useParkingEntry();
  const exit = useParkingExit();

  return (
    <Stack gap="lg">
      <PageHeader title="Parking" description="Vehicle entry and exit, flat rate per hour" />

      {isLoading && <Skeleton className="h-10 w-64" />}
      {!isLoading && (!properties || properties.length === 0) && (
        <EmptyState title="No properties yet" description="Add a property first." />
      )}
      {!isLoading && properties && properties.length > 0 && (
        <>
          <Select
            label="Property"
            value={selected}
            onChange={(e) => setPropertyId(e.target.value)}
            options={properties.map((p) => ({ value: p.id, label: p.name }))}
          />

          <Card>
            <Stack gap="md">
              <h3 className="font-medium">Vehicle Entry</h3>
              <Stack direction="row" gap="sm" align="end">
                <Input
                  label="Vehicle Plate"
                  value={plate}
                  onChange={(e) => setPlate(e.target.value)}
                />
                <Button
                  disabled={!plate.trim()}
                  loading={entry.isPending}
                  onClick={async () => {
                    try {
                      const session = await entry.mutateAsync({
                        propertyId: selected,
                        vehiclePlate: plate,
                      });
                      setLastSession(session);
                      toast.success(`Entry recorded — session ${session.id.slice(0, 8)}`);
                      setPlate("");
                    } catch {
                      toast.error("Failed to record entry — vehicle may already be parked");
                    }
                  }}
                >
                  Record Entry
                </Button>
              </Stack>
              {lastSession && (
                <p className="text-sm text-muted-foreground">
                  Session ID: {lastSession.id} (copy this to record the exit later)
                </p>
              )}
            </Stack>
          </Card>

          <Card>
            <Stack gap="md">
              <h3 className="font-medium">Vehicle Exit</h3>
              <Stack direction="row" gap="sm" align="end">
                <Input
                  label="Session ID"
                  value={exitId}
                  onChange={(e) => setExitId(e.target.value)}
                />
                <Button
                  disabled={!exitId.trim()}
                  loading={exit.isPending}
                  onClick={async () => {
                    try {
                      const session = await exit.mutateAsync(exitId);
                      setExitedSession(session);
                      toast.success(`Fee charged: $${session.feeCharged}`);
                      setExitId("");
                    } catch {
                      toast.error("Failed to record exit");
                    }
                  }}
                >
                  Record Exit
                </Button>
              </Stack>
              {exitedSession && (
                <p className="text-sm">
                  {exitedSession.vehiclePlate} — fee charged: ${exitedSession.feeCharged}
                </p>
              )}
            </Stack>
          </Card>
        </>
      )}
    </Stack>
  );
}
