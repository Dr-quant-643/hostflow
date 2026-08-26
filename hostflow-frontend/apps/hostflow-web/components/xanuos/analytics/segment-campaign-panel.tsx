"use client";

import { useState } from "react";
import { Card, Stack, Input, Textarea, Select, Button, Badge, DataTable, EmptyState, Skeleton, toast } from "@hostflow/ui";
import {
  useSegmentCampaigns,
  useCreateSegmentCampaign,
  useSendSegmentCampaign,
} from "@hostflow/api-client/src/hooks/use-segment-campaigns";
import type { SegmentCampaignResponse, SegmentCampaignTarget } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";
import { GUEST_SEGMENT_LABEL } from "@/lib/status-badge";

const TARGET_OPTIONS: { value: SegmentCampaignTarget; label: string }[] = [
  { value: "ALL", label: "All guests" },
  { value: "VIP", label: GUEST_SEGMENT_LABEL.VIP },
  { value: "REPEAT", label: GUEST_SEGMENT_LABEL.REPEAT },
  { value: "AT_RISK", label: GUEST_SEGMENT_LABEL.AT_RISK },
  { value: "NEW", label: GUEST_SEGMENT_LABEL.NEW },
  { value: "ACTIVE_TENANT", label: GUEST_SEGMENT_LABEL.ACTIVE_TENANT },
];

function CreateCampaignForm() {
  const [targetSegment, setTargetSegment] = useState<SegmentCampaignTarget>("AT_RISK");
  const [subject, setSubject] = useState("");
  const [body, setBody] = useState("");
  const create = useCreateSegmentCampaign();

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subject.trim() || !body.trim()) return;
    try {
      await create.mutateAsync({ targetSegment, subject, body });
      setSubject("");
      setBody("");
      toast.success("Campaign saved as a draft");
    } catch {
      toast.error("Couldn't save the campaign. Please try again.");
    }
  };

  return (
    <Card className="p-6">
      <form onSubmit={onSubmit}>
        <Stack gap="md">
          <p className="font-medium">New campaign</p>
          <Select
            label="Send to"
            value={targetSegment}
            onChange={(e) => setTargetSegment(e.target.value as SegmentCampaignTarget)}
            options={TARGET_OPTIONS}
          />
          <Input
            label="Subject"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            placeholder="e.g. We miss you — 15% off your next stay"
          />
          <Textarea
            label="Message"
            value={body}
            onChange={(e) => setBody(e.target.value)}
            placeholder="Hi {{guest_name}}, it's been a while..."
            rows={4}
          />
          <p className="text-xs text-muted-foreground">
            Use <code>{"{{guest_name}}"}</code> anywhere in the message to personalize it per guest.
          </p>
          <Button type="submit" disabled={!subject.trim() || !body.trim()} loading={create.isPending} className="w-fit">
            Save as draft
          </Button>
        </Stack>
      </form>
    </Card>
  );
}

function SendButton({ campaign }: { campaign: SegmentCampaignResponse }) {
  const send = useSendSegmentCampaign(campaign.id);
  if (campaign.status === "SENT") {
    return (
      <span className="text-xs text-muted-foreground">
        Sent to {campaign.recipientCount} guest{campaign.recipientCount === 1 ? "" : "s"}
      </span>
    );
  }
  return (
    <Button
      size="sm"
      loading={send.isPending}
      onClick={async () => {
        try {
          await send.mutateAsync();
          toast.success("Campaign sent");
        } catch {
          toast.error("Couldn't send the campaign");
        }
      }}
    >
      Send now
    </Button>
  );
}

const columns: ColumnDef<SegmentCampaignResponse>[] = [
  { accessorKey: "subject", header: "Subject" },
  {
    accessorKey: "targetSegment",
    header: "Audience",
    cell: ({ row }) =>
      row.original.targetSegment === "ALL" ? "All guests" : GUEST_SEGMENT_LABEL[row.original.targetSegment],
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge variant={row.original.status === "SENT" ? "success" : "outline"}>{row.original.status}</Badge>,
  },
  {
    id: "action",
    header: "",
    cell: ({ row }) => <SendButton campaign={row.original} />,
  },
];

function CampaignList() {
  const { data, isLoading, isError } = useSegmentCampaigns();

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) return <EmptyState title="Couldn't load campaigns" description="Try refreshing." />;
  if (!data || data.length === 0) {
    return <EmptyState title="No campaigns yet" description="Create one above to reach a segment of your guests." />;
  }

  return <DataTable columns={columns} data={data} />;
}

export function SegmentCampaignPanel() {
  return (
    <Stack gap="md">
      <CreateCampaignForm />
      <CampaignList />
    </Stack>
  );
}
