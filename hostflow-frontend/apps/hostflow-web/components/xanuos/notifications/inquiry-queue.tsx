"use client";

import { useState } from "react";
import Link from "next/link";
import { Stack, Card, Badge, Textarea, Button, Skeleton, toast } from "@hostflow/ui";
import { useOwnerRentalInquiries, useReplyToRentalInquiry } from "@hostflow/api-client/src/hooks/use-rental";
import type { MyRentalInquiry } from "@hostflow/types";

// FIFO queue across every property the owner has -- oldest inquiry first, so
// working top-to-bottom each morning clears the backlog in the order guests
// actually asked. Lives in the Notifications tab so an owner has one place to
// see "who asked about what" and reply, instead of checking each property's
// page individually.
export function InquiryQueue() {
  const { data, isLoading } = useOwnerRentalInquiries();

  if (isLoading) return <Skeleton className="h-24 w-full" />;
  if (!data || data.length === 0) return null;

  const openCount = data.filter((i) => i.status === "OPEN").length;

  return (
    <Stack gap="sm">
      <div className="flex items-center justify-between">
        <h3 className="font-medium">Rental Inquiries</h3>
        {openCount > 0 && (
          <Badge variant="warning">
            You have {openCount} {openCount === 1 ? "inquiry" : "inquiries"} awaiting reply
          </Badge>
        )}
      </div>
      <Stack gap="sm">
        {data.map((inquiry, index) => (
          <InquiryRow key={inquiry.id} inquiry={inquiry} position={index + 1} />
        ))}
      </Stack>
    </Stack>
  );
}

function InquiryRow({ inquiry, position }: { inquiry: MyRentalInquiry; position: number }) {
  const [replyText, setReplyText] = useState("");
  const replyMutation = useReplyToRentalInquiry(inquiry.propertyId);

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
        <div className="flex items-center justify-between gap-2">
          <div className="flex items-center gap-2 min-w-0">
            <span className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
              {position}
            </span>
            <Link href={`/xanuos/properties/${inquiry.propertyId}`} className="truncate text-sm font-medium hover:underline">
              {inquiry.propertyName}
            </Link>
          </div>
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
