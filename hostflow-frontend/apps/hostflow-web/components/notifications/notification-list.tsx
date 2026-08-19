"use client";

import { Skeleton, EmptyState, Badge, Stack, Card, Button } from "@hostflow/ui";
import {
  useNotifications,
  useMarkNotificationRead,
} from "@hostflow/api-client/src/hooks/use-notifications";

export function NotificationList() {
  const { data, isLoading, isError } = useNotifications();
  const markRead = useMarkNotificationRead();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState
        title="Notifications aren't available yet"
        description="This depends on backend delivery wiring that isn't finished (Open Items B/D)."
      />
    );
  }
  if (!data || data.content.length === 0) {
    return <EmptyState title="No notifications" />;
  }

  return (
    <Stack gap="sm">
      {data.content.map((n) => (
        <Card key={n.id}>
          <Stack direction="row" gap="md" align="center">
            <Badge variant={n.read ? "outline" : "default"}>{n.channel}</Badge>
            <div className="flex-1">
              <p className="font-medium">{n.subject}</p>
              <p className="text-sm text-muted-foreground">
                {n.status} · {n.createdAt}
              </p>
            </div>
            {!n.read && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => markRead.mutate(n.id)}
              >
                Mark read
              </Button>
            )}
          </Stack>
        </Card>
      ))}
    </Stack>
  );
}
