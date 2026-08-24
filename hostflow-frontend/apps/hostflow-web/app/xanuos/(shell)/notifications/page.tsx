import { PageHeader, Stack } from "@hostflow/ui";
import { NotificationList } from "@/components/xanuos/notifications/notification-list";
import { InquiryQueue } from "@/components/xanuos/notifications/inquiry-queue";

export default function NotificationsPage() {
  return (
    <div>
      <PageHeader
        title="Notifications"
        description="Rental inquiries awaiting your reply, plus the delivery log across channels"
      />
      <Stack gap="lg">
        <InquiryQueue />
        <NotificationList />
      </Stack>
    </div>
  );
}
