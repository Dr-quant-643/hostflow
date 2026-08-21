"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  maintenanceScheduleFormSchema,
  type MaintenanceScheduleFormValues,
} from "@hostflow/validation";
import { Button, Input, Select, Stack, toast } from "@hostflow/ui";
import { useCreateMaintenanceSchedule } from "@hostflow/api-client/src/hooks/use-maintenance";

const CATEGORY_OPTIONS = [
  { value: "PLUMBING", label: "Plumbing" },
  { value: "ELECTRICAL", label: "Electrical" },
  { value: "HVAC", label: "HVAC" },
  { value: "APPLIANCE", label: "Appliance" },
  { value: "STRUCTURAL", label: "Structural" },
  { value: "PEST_CONTROL", label: "Pest Control" },
  { value: "CLEANING", label: "Cleaning" },
  { value: "OTHER", label: "Other" },
];

export function MaintenanceScheduleForm({ propertyId }: { propertyId: string }) {
  const createSchedule = useCreateMaintenanceSchedule();

  const form = useForm<MaintenanceScheduleFormValues>({
    resolver: zodResolver(maintenanceScheduleFormSchema),
    defaultValues: {
      propertyId,
      category: "OTHER",
      title: "",
      intervalDays: 90,
      firstDueDate: new Date().toISOString().slice(0, 10),
    },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createSchedule.mutateAsync({ ...values, propertyId });
      toast.success("Preventive maintenance schedule created");
      form.reset({
        propertyId,
        category: "OTHER",
        title: "",
        intervalDays: 90,
        firstDueDate: new Date().toISOString().slice(0, 10),
      });
    } catch {
      toast.error("Failed to create schedule");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Select label="Category" {...form.register("category")} options={CATEGORY_OPTIONS} />
        <Input
          label="Title"
          {...form.register("title")}
          error={form.formState.errors.title?.message}
        />
        <Input label="Interval (days)" type="number" {...form.register("intervalDays")} />
        <Input label="First Due Date" type="date" {...form.register("firstDueDate")} />
        <Button type="submit" loading={createSchedule.isPending}>
          Create Schedule
        </Button>
      </Stack>
    </form>
  );
}
