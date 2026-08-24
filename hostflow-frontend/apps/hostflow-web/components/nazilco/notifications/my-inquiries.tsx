"use client";

import { Stack, Card, Badge, Skeleton } from "@hostflow/ui";
import { useMyRentalInquiries } from "@hostflow/api-client/src/hooks/use-rental";

// A guest's sent rental inquiries + the owner's reply, if any -- lives in the
// Notifications tab (FIFO, oldest first) so checking "did they answer" is one
// stop instead of hunting through trips/email. The in-app alternative to
// only finding out via email.
export function MyInquiries() {
  const { data, isLoading } = useMyRentalInquiries();

  if (isLoading) return <Skeleton className="h-24 w-full" />;
  if (!data || data.length === 0) return null;

  const repliedCount = data.filter((i) => i.status === "REPLIED").length;

  return (
    <Stack gap="sm">
      <div className="flex items-center justify-between">
        <h3 className="font-medium">Rental Inquiries</h3>
        {repliedCount > 0 && (
          <Badge variant="success">
            You have {repliedCount} {repliedCount === 1 ? "reply" : "replies"}
          </Badge>
        )}
      </div>
      <Stack gap="sm">
        {data.map((inquiry, index) => (
          <Card key={inquiry.id} className="p-4">
            <Stack gap="sm">
              <div className="flex items-center justify-between gap-2">
                <div className="flex items-center gap-2 min-w-0">
                  <span className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
                    {index + 1}
                  </span>
                  <p className="truncate text-sm font-medium">{inquiry.propertyName}</p>
                </div>
                <Badge variant={inquiry.status === "OPEN" ? "warning" : "success"}>
                  {inquiry.status === "OPEN" ? "Awaiting reply" : "Replied"}
                </Badge>
              </div>
              <p className="text-sm text-muted-foreground">{inquiry.message}</p>
              {inquiry.status === "REPLIED" && (
                <div className="rounded-lg bg-muted p-3 text-sm">
                  <span className="font-medium">Owner replied: </span>
                  {inquiry.replyMessage}
                </div>
              )}
            </Stack>
          </Card>
        ))}
      </Stack>
    </Stack>
  );
}
