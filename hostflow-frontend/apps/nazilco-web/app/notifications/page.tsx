"use client";

import { motion } from "framer-motion";
import { PageHeader, Stack, Card, Badge, Skeleton, EmptyState } from "@hostflow/ui";
import { CalendarCheck, BellRing, Star, Receipt as ReceiptIcon, Mail, Smartphone, MessageSquare } from "lucide-react";
import { useDemoMyNotifications } from "@/lib/demo-hooks";
import { BrandBackground } from "@/components/layout/brand-background";

const TEMPLATE_META: Record<string, { icon: typeof BellRing; title: string }> = {
  BOOKING_CONFIRMED: { icon: CalendarCheck, title: "Booking confirmed" },
  CHECK_IN_REMINDER: { icon: BellRing, title: "Check-in reminder" },
  REVIEW_REQUEST: { icon: Star, title: "Leave a review" },
  PAYMENT_RECEIPT: { icon: ReceiptIcon, title: "Payment receipt" },
};

const CHANNEL_ICON: Record<string, typeof Mail> = {
  EMAIL: Mail,
  PUSH: Smartphone,
  SMS: MessageSquare,
  WHATSAPP: MessageSquare,
};

function formatRelativeTime(iso: string): string {
  const diffMs = Date.now() - new Date(iso).getTime();
  const minutes = Math.round(diffMs / 60000);
  if (minutes < 60) return `${minutes} min ago`;
  const hours = Math.round(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.round(hours / 24);
  if (days < 30) return `${days}d ago`;
  return new Date(iso).toLocaleDateString();
}

export default function NotificationsPage() {
  const { data, isLoading, isError } = useDemoMyNotifications();

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-3xl p-6">
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <PageHeader title="Notifications" description="Booking updates and account alerts" />
        </motion.div>

        {isLoading && <Skeleton className="h-64 w-full" />}
        {!isLoading && isError && (
          <EmptyState title="Couldn't load your notifications" description="Try refreshing." />
        )}
        {!isLoading && data && data.length === 0 && (
          <EmptyState title="No notifications yet" description="You're all caught up." />
        )}
        {!isLoading && data && data.length > 0 && (
          <Stack gap="sm">
            {data.map((n, i) => {
              const meta = TEMPLATE_META[n.templateCode] ?? { icon: BellRing, title: n.templateCode };
              const Icon = meta.icon;
              const ChannelIcon = CHANNEL_ICON[n.channel] ?? Mail;
              return (
                <motion.div
                  key={n.id}
                  initial={{ opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.35, delay: i * 0.05 }}
                >
                  <Card className="flex items-center gap-4 p-4 transition-shadow hover:shadow-md">
                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-sapphire-50 text-sapphire-600">
                      <Icon className="h-5 w-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="font-medium">{meta.title}</p>
                      <p className="flex items-center gap-1 text-sm text-muted-foreground">
                        <ChannelIcon className="h-3.5 w-3.5" />
                        {n.channel.charAt(0) + n.channel.slice(1).toLowerCase()} · {formatRelativeTime(n.createdAt)}
                      </p>
                    </div>
                    <Badge variant="secondary">{n.status.charAt(0) + n.status.slice(1).toLowerCase()}</Badge>
                  </Card>
                </motion.div>
              );
            })}
          </Stack>
        )}
      </Stack>
    </>
  );
}
