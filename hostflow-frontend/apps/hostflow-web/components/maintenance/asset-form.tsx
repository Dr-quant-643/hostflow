"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { assetFormSchema, type AssetFormValues } from "@hostflow/validation";
import { Button, Input, Stack, toast } from "@hostflow/ui";
import { useCreateAsset } from "@hostflow/api-client/src/hooks/use-maintenance";

export function AssetForm({ propertyId }: { propertyId: string }) {
  const createAsset = useCreateAsset();

  const form = useForm<AssetFormValues>({
    resolver: zodResolver(assetFormSchema),
    defaultValues: { propertyId, name: "", category: "", serialNumber: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createAsset.mutateAsync({ ...values, propertyId });
      toast.success("Asset added");
      form.reset({ propertyId, name: "", category: "", serialNumber: "" });
    } catch {
      toast.error("Failed to add asset");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Input
          label="Name"
          {...form.register("name")}
          error={form.formState.errors.name?.message}
        />
        <Input label="Category" {...form.register("category")} />
        <Input label="Serial Number" {...form.register("serialNumber")} />
        <Input label="Warranty Expiry" type="date" {...form.register("warrantyExpiryDate")} />
        <Button type="submit" loading={createAsset.isPending}>
          Add Asset
        </Button>
      </Stack>
    </form>
  );
}
