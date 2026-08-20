"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { retailUnitFormSchema, type RetailUnitFormValues } from "@hostflow/validation";
import { Button, Input, Stack, toast } from "@hostflow/ui";
import { useCreateRetailUnit } from "@hostflow/api-client/src/hooks/use-mall";

export function RetailUnitForm({ propertyId }: { propertyId: string }) {
  const createUnit = useCreateRetailUnit();

  const form = useForm<RetailUnitFormValues>({
    resolver: zodResolver(retailUnitFormSchema),
    defaultValues: { propertyId, unitNumber: "", sizeSqm: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createUnit.mutateAsync({ ...values, propertyId });
      toast.success("Retail unit added");
      form.reset({ propertyId, unitNumber: "", sizeSqm: "" });
    } catch {
      toast.error("Failed to add retail unit");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Input
          label="Unit Number"
          {...form.register("unitNumber")}
          error={form.formState.errors.unitNumber?.message}
        />
        <Input label="Size (sqm)" {...form.register("sizeSqm")} />
        <Button type="submit" loading={createUnit.isPending}>
          Add Unit
        </Button>
      </Stack>
    </form>
  );
}
