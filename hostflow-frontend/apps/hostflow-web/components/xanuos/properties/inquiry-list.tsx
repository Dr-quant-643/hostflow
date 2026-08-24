"use client";

import { useState } from "react";
import { Stack, Card, Badge, Textarea, Button, Skeleton, toast } from "@hostflow/ui";
import { useRentalInquiries, useReplyToRentalInquiry } from "@hostflow/api-client/src/hooks/use-rental";
import type { RentalInquiryResponse } from "@hostflow/types";

// The in-app answer to "how does an owner reply to a rental inquiry" -- the
// generic /notifications inbox only ever carries template code/channel/
// status (see NotificationLog), never message content, so this reads
// directly from the persisted RentalInquiry record instead.
export function InquiryList({ propertyId }: { propertyId: string }) {
  const { data, isLoading } = useRentalInquiries(propertyId);
  const inquiries = data?.content ?? [];

  if (isLoading) return <Skeleton className="h-24 w-full" />;
  if (inquiries.length === 0) {
    return <p className="text-sm text-muted-foreground">No inquiries yet for this property.</p>;
  }

  return (
    <Stack gap="md">
      {inquiries.map((inquiry) => (
        <InquiryRow key={inquiry.id} inquiry={inquiry} propertyId={propertyId} />
      ))}
    </Stack>
  );
}

function InquiryRow({ inquiry, propertyId }: { inquiry: RentalInquiryResponse; propertyId: string }) {
  const [replyText, setReplyText] = useState("");
  const replyMutation = useReplyToRentalInquiry(propertyId);

  const onReply = async () => {
    if (!replyText.trim()) return;
    try {
      await replyMutation.mutateAsync({ id: inquiry.id, message: replyText });
      toast.success("Reply sent to the guest");
    } catch {
      toast.error("Couldn't send your reply. Please try again.");
    }
  };

  return (
    <Card className="p-4">
      <Stack gap="sm">
        <div className="flex items-center justify-between">
          <Badge variant={inquiry.status === "OPEN" ? "warning" : "success"}>
            {inquiry.status === "OPEN" ? "Awaiting reply" : "Replied"}
          </Badge>
        </div>
        <p className="text-sm">{inquiry.message}</p>
        {inquiry.status === "REPLIED" ? (
          <div className="rounded-lg bg-muted p-3 text-sm">
            <span className="font-medium">Your reply: </span>
            {inquiry.replyMessage}
          </div>
        ) : (
          <Stack gap="sm">
            <Textarea
              placeholder="Reply to this inquiry..."
              value={replyText}
              onChange={(e) => setReplyText(e.target.value)}
              rows={2}
            />
            <Button
              variant="outline"
              disabled={!replyText.trim()}
              loading={replyMutation.isPending}
              onClick={onReply}
            >
              Send Reply
            </Button>
          </Stack>
        )}
      </Stack>
    </Card>
  );
}
