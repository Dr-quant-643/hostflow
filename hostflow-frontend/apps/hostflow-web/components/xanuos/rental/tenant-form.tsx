"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { rentalTenantFormSchema, type RentalTenantFormValues } from "@hostflow/validation";
import { Button, Input, Stack, toast } from "@hostflow/ui";
import { useCreateRentalTenant } from "@hostflow/api-client/src/hooks/use-rental";

export function TenantForm() {
  const createTenant = useCreateRentalTenant();

  const form = useForm<RentalTenantFormValues>({
    resolver: zodResolver(rentalTenantFormSchema),
    defaultValues: { fullName: "", email: "", phone: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createTenant.mutateAsync(values);
      toast.success("Tenant added");
      form.reset({ fullName: "", email: "", phone: "" });
    } catch {
      toast.error("Failed to add tenant");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Input
          label="Full Name"
          {...form.register("fullName")}
          error={form.formState.errors.fullName?.message}
        />
        <Input label="Email" {...form.register("email")} error={form.formState.errors.email?.message} />
        <Input label="Phone" {...form.register("phone")} />
        <Button type="submit" loading={createTenant.isPending}>
          Add Tenant
        </Button>
      </Stack>
    </form>
  );
}
