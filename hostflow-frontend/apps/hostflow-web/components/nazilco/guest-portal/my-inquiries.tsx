"use client";

import { Stack, Card, Badge, Skeleton } from "@hostflow/ui";
import { useMyRentalInquiries } from "@hostflow/api-client/src/hooks/use-rental";

// A guest's sent rental inquiries + the owner's reply, if any -- the in-app
// alternative to only finding out "did they answer" via email.
export function MyInquiries() {
  const { data, isLoading } = useMyRentalInquiries();

  if (isLoading) return <Skeleton className="h-24 w-full" />;
  if (!data || data.length === 0) return null;

  return (
    <Stack gap="sm">
      <h3 className="font-medium">Rental Inquiries</h3>
      <Stack gap="sm">
        {data.map((inquiry) => (
          <Card key={inquiry.id} className="p-4">
            <Stack gap="sm">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium">{inquiry.propertyName}</p>
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
