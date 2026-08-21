"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { PageHeader,
  Skeleton,
  EmptyState,
  Timeline,
  Stack,
  Badge,
  Button,
  Select,
  Textarea,
  Card, toast } from "@hostflow/ui";
import {
  useContact,
  useContactInteractions,
  useQualifyContact,
  useLogInteraction,
} from "@hostflow/api-client/src/hooks/use-crm";

export default function ContactDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: contact, isLoading, isError } = useContact(id);
  const { data: interactions } = useContactInteractions(id);
  const qualify = useQualifyContact(id);
  const logInteraction = useLogInteraction(id);

  const [interactionType, setInteractionType] = useState("NOTE");
  const [notes, setNotes] = useState("");

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !contact) return <EmptyState title="Contact not found" />;

  return (
    <Stack gap="lg">
      <PageHeader
        title={contact.fullName}
        description={contact.email}
        actions={
          <Stack direction="row" gap="sm" align="center">
            <Badge>{contact.status}</Badge>
            {contact.status === "LEAD" && (
              <Button
                loading={qualify.isPending}
                onClick={async () => {
                  try {
                    await qualify.mutateAsync();
                    toast.success("Contact qualified");
                  } catch {
                    toast.error("Failed to qualify");
                  }
                }}
              >
                Qualify
              </Button>
            )}
          </Stack>
        }
      />

      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Log Interaction</h3>
          <Stack direction="row" gap="sm" align="end">
            <Select
              label="Type"
              value={interactionType}
              onChange={(e) => setInteractionType(e.target.value)}
              options={[
                { value: "CALL", label: "Call" },
                { value: "EMAIL", label: "Email" },
                { value: "MEETING", label: "Meeting" },
                { value: "NOTE", label: "Note" },
                { value: "WHATSAPP_MESSAGE", label: "WhatsApp Message" },
              ]}
            />
          </Stack>
          <Textarea
            label="Notes"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
          />
          <Button
            disabled={!notes.trim()}
            loading={logInteraction.isPending}
            onClick={async () => {
              try {
                await logInteraction.mutateAsync({ type: interactionType, notes });
                toast.success("Interaction logged");
                setNotes("");
              } catch {
                toast.error("Failed to log interaction");
              }
            }}
          >
            Log Interaction
          </Button>
        </Stack>
      </Card>

      {interactions && interactions.length > 0 ? (
        <Timeline
          events={interactions.map((i) => ({
            id: i.id,
            title: i.type,
            description: i.notes,
            timestamp: i.occurredAt,
          }))}
        />
      ) : (
        <EmptyState title="No interactions logged yet" />
      )}
    </Stack>
  );
}
