"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import {
  contactFormSchema,
  type ContactFormValues,
} from "@hostflow/validation";
import { Button, Input, Stack } from "@hostflow/ui";
import { useCreateContact } from "@hostflow/api-client/src/hooks/use-crm";

export function ContactForm() {
  const router = useRouter();
  const createContact = useCreateContact();

  const form = useForm<ContactFormValues>({
    resolver: zodResolver(contactFormSchema),
    defaultValues: { fullName: "", email: "", phone: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      const created = await createContact.mutateAsync(values);
      toast.success("Contact created");
      router.push(`/crm/${created.id}`);
    } catch {
      toast.error("Failed to create contact");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Input
          label="Full Name"
          {...form.register("fullName")}
          error={form.formState.errors.fullName?.message}
        />
        <Input
          label="Email"
          {...form.register("email")}
          error={form.formState.errors.email?.message}
        />
        <Input
          label="Phone"
          {...form.register("phone")}
          error={form.formState.errors.phone?.message}
        />
        <Input
          label="Source"
          placeholder="e.g. website, referral, walk-in"
          {...form.register("source")}
          error={form.formState.errors.source?.message}
        />
        <Button type="submit" loading={createContact.isPending}>
          Create Contact
        </Button>
      </Stack>
    </form>
  );
}
