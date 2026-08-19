"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { workOrderFormSchema, type WorkOrderFormValues } from "@hostflow/validation";
import { Button, Input, Textarea, Select, Stack } from "@hostflow/ui";
import { useCreateWorkOrder } from "@hostflow/api-client/src/hooks/use-maintenance";

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

const PRIORITY_OPTIONS = [
  { value: "LOW", label: "Low" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HIGH", label: "High" },
  { value: "EMERGENCY", label: "Emergency" },
];

export function WorkOrderForm({ propertyId }: { propertyId: string }) {
  const createWorkOrder = useCreateWorkOrder();

  const form = useForm<WorkOrderFormValues>({
    resolver: zodResolver(workOrderFormSchema),
    defaultValues: {
      propertyId,
      category: "OTHER",
      title: "",
      description: "",
      priority: "MEDIUM",
    },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createWorkOrder.mutateAsync({ ...values, propertyId });
      toast.success("Work order created");
      form.reset({
        propertyId,
        category: "OTHER",
        title: "",
        description: "",
        priority: "MEDIUM",
      });
    } catch {
      toast.error("Failed to create work order");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Stack direction="row" gap="sm" align="end">
          <Select label="Category" {...form.register("category")} options={CATEGORY_OPTIONS} />
          <Select label="Priority" {...form.register("priority")} options={PRIORITY_OPTIONS} />
        </Stack>
        <Input
          label="Title"
          {...form.register("title")}
          error={form.formState.errors.title?.message}
        />
        <Textarea label="Description" {...form.register("description")} />
        <Button type="submit" loading={createWorkOrder.isPending}>
          Report Issue
        </Button>
      </Stack>
    </form>
  );
}
