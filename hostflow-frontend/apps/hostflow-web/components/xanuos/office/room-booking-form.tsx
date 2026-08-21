"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { roomBookingFormSchema, type RoomBookingFormValues } from "@hostflow/validation";
import { Button, Input, Select, Stack, Card, toast } from "@hostflow/ui";
import { useCreateRoomBooking, useCancelRoomBooking } from "@hostflow/api-client/src/hooks/use-office";
import type { MeetingRoomResponse } from "@hostflow/types";
import { useState } from "react";

export function RoomBookingForm({ rooms }: { rooms: MeetingRoomResponse[] }) {
  const createBooking = useCreateRoomBooking();
  const cancelBooking = useCancelRoomBooking();
  const [cancelId, setCancelId] = useState("");

  const form = useForm<RoomBookingFormValues>({
    resolver: zodResolver(roomBookingFormSchema),
    defaultValues: { roomId: rooms[0]?.id, startsAt: "", endsAt: "", purpose: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createBooking.mutateAsync(values);
      toast.success("Room booked");
    } catch {
      toast.error("Failed to book room — the slot may already be taken");
    }
  });

  return (
    <Card>
      <Stack gap="md">
        <h3 className="font-medium">Book a Room</h3>
        <form onSubmit={onSubmit}>
          <Stack direction="row" gap="sm" align="end">
            <Select
              label="Room"
              {...form.register("roomId")}
              options={rooms.map((r) => ({ value: r.id, label: r.name }))}
            />
            <Input label="Starts" type="datetime-local" {...form.register("startsAt")} />
            <Input label="Ends" type="datetime-local" {...form.register("endsAt")} />
            <Input label="Purpose" {...form.register("purpose")} />
            <Button type="submit" loading={createBooking.isPending}>
              Book
            </Button>
          </Stack>
        </form>

        <Stack direction="row" gap="sm" align="end">
          <Input
            label="Cancel Booking (ID)"
            value={cancelId}
            onChange={(e) => setCancelId(e.target.value)}
          />
          <Button
            variant="destructive"
            disabled={!cancelId.trim()}
            loading={cancelBooking.isPending}
            onClick={async () => {
              try {
                await cancelBooking.mutateAsync(cancelId);
                toast.success("Booking cancelled");
                setCancelId("");
              } catch {
                toast.error("Failed to cancel booking");
              }
            }}
          >
            Cancel
          </Button>
        </Stack>
      </Stack>
    </Card>
  );
}
