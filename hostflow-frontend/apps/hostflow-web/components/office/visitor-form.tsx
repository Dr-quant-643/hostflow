"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { visitorFormSchema, type VisitorFormValues } from "@hostflow/validation";
import { Button, Input, Stack, toast } from "@hostflow/ui";
import { useRegisterVisitor } from "@hostflow/api-client/src/hooks/use-office";

export function VisitorForm({ propertyId }: { propertyId: string }) {
  const registerVisitor = useRegisterVisitor();

  const form = useForm<VisitorFormValues>({
    resolver: zodResolver(visitorFormSchema),
    defaultValues: { propertyId, fullName: "", company: "", expectedAt: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await registerVisitor.mutateAsync({ ...values, propertyId });
      toast.success("Visitor registered");
      form.reset({ propertyId, fullName: "", company: "", expectedAt: "" });
    } catch {
      toast.error("Failed to register visitor");
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
        <Input label="Company" {...form.register("company")} />
        <Input label="Expected At" type="datetime-local" {...form.register("expectedAt")} />
        <Button type="submit" loading={registerVisitor.isPending}>
          Register Visitor
        </Button>
      </Stack>
    </form>
  );
}
