// Mirrors GuestNotificationQueries.InboxNotificationRow, the shape
// NotificationInboxController returns for a NAZILCO guest caller (tenant-less
// JWT). Staff callers get a different Page<NotificationLog> shape from the
// same endpoint — this type only covers the guest branch.

export interface GuestNotificationRow {
  id: string;
  templateCode: string;
  channel: string;
  status: string;
  createdAt: string;
}
