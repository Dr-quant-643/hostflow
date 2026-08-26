"use client";

import { Suspense, useState } from "react";
import { useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  raiseTicketFormSchema,
  type RaiseTicketFormValues,
} from "@hostflow/validation";
import { PageHeader, Stack, Card, Input, Textarea, Select, Button, toast } from "@hostflow/ui";
import { useRaiseSupportTicket } from "@hostflow/api-client/src/hooks/use-guest-support-ticket";
import { MaintenanceRequestForm } from "@/components/nazilco/support/maintenance-request-form";

const PRIORITY_OPTIONS = [
  { value: "LOW", label: "Low" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HIGH", label: "High" },
  { value: "URGENT", label: "Urgent" },
];

export default function SupportPage() {
  return (
    <Suspense fallback={null}>
      <SupportPageContent />
    </Suspense>
  );
}

// useSearchParams() (used for the propertyId maintenance deep link) requires
// a Suspense boundary during static generation, same reasoning as the
// property detail page's checkIn/checkOut deep link.
function SupportPageContent() {
  const propertyId = useSearchParams().get("propertyId");
  const [submitted, setSubmitted] = useState(false);
  const raiseTicket = useRaiseSupportTicket();
  const form = useForm<RaiseTicketFormValues>({
    resolver: zodResolver(raiseTicketFormSchema),
    defaultValues: { subject: "", description: "", priority: "MEDIUM" },
  });

  if (submitted) {
    return (
      <Stack gap="lg" className="p-6 text-center">
        <PageHeader title="Ticket submitted" description="Our team will get back to you soon." />
      </Stack>
    );
  }

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await raiseTicket.mutateAsync(values);
      setSubmitted(true);
      toast.success("Ticket submitted");
    } catch {
      toast.error("Couldn't submit your ticket. Please try again.");
    }
  });

  return (
    <Stack gap="lg" className="mx-auto max-w-lg p-6">
      <PageHeader title="Support" description="Get help from our support team, or report an issue with your stay" />
      {propertyId && <MaintenanceRequestForm propertyId={propertyId} />}
      <Card className="p-6">
        <form onSubmit={onSubmit}>
          <Stack gap="md">
            <Input
              label="Subject"
              {...form.register("subject")}
              error={form.formState.errors.subject?.message}
            />
            <Textarea
              label="Description (optional)"
              {...form.register("description")}
              error={form.formState.errors.description?.message}
            />
            <Select
              label="Priority"
              options={PRIORITY_OPTIONS}
              {...form.register("priority")}
              error={form.formState.errors.priority?.message}
            />
            <Button type="submit" loading={raiseTicket.isPending}>
              Submit Ticket
            </Button>
          </Stack>
        </form>
      </Card>
    </Stack>
  );
}
