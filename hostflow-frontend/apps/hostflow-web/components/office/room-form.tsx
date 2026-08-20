"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { meetingRoomFormSchema, type MeetingRoomFormValues } from "@hostflow/validation";
import { Button, Input, Stack, toast } from "@hostflow/ui";
import { useCreateMeetingRoom } from "@hostflow/api-client/src/hooks/use-office";

export function RoomForm({ propertyId }: { propertyId: string }) {
  const createRoom = useCreateMeetingRoom();

  const form = useForm<MeetingRoomFormValues>({
    resolver: zodResolver(meetingRoomFormSchema),
    defaultValues: { propertyId, name: "", capacity: 4 },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createRoom.mutateAsync({ ...values, propertyId });
      toast.success("Room added");
      form.reset({ propertyId, name: "", capacity: 4 });
    } catch {
      toast.error("Failed to add room");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Input
          label="Room Name"
          {...form.register("name")}
          error={form.formState.errors.name?.message}
        />
        <Input label="Capacity" type="number" {...form.register("capacity")} />
        <Button type="submit" loading={createRoom.isPending}>
          Add Room
        </Button>
      </Stack>
    </form>
  );
}
