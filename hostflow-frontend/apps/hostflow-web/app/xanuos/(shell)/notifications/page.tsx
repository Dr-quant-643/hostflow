import { PageHeader } from "@hostflow/ui";
import { NotificationList } from "@/components/xanuos/notifications/notification-list";

export default function NotificationsPage() {
  return (
    <div>
      <PageHeader
        title="Notifications"
        description="Delivery log across channels"
      />
      <NotificationList />
    </div>
  );
}
