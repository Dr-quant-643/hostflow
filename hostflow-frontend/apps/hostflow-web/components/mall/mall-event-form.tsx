"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { mallEventFormSchema, type MallEventFormValues } from "@hostflow/validation";
import { Button, Input, Textarea, Stack } from "@hostflow/ui";
import { useCreateMallEvent } from "@hostflow/api-client/src/hooks/use-mall";

export function MallEventForm({ propertyId }: { propertyId: string }) {
  const createEvent = useCreateMallEvent();

  const form = useForm<MallEventFormValues>({
    resolver: zodResolver(mallEventFormSchema),
    defaultValues: { propertyId, title: "", description: "", startsAt: "", endsAt: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createEvent.mutateAsync({ ...values, propertyId });
      toast.success("Event created");
      form.reset({ propertyId, title: "", description: "", startsAt: "", endsAt: "" });
    } catch {
      toast.error("Failed to create event");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Stack direction="row" gap="sm" align="end">
          <Input
            label="Title"
            {...form.register("title")}
            error={form.formState.errors.title?.message}
          />
          <Input label="Starts" type="datetime-local" {...form.register("startsAt")} />
          <Input label="Ends" type="datetime-local" {...form.register("endsAt")} />
        </Stack>
        <Textarea label="Description" {...form.register("description")} />
        <Button type="submit" loading={createEvent.isPending}>
          Create Event
        </Button>
      </Stack>
    </form>
  );
}
