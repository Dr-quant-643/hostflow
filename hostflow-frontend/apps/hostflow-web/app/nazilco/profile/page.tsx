"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  PageHeader,
  Card,
  Stack,
} from "@hostflow/ui";
import {
  Heart,
  Briefcase,
  Receipt,
  Bell,
  LifeBuoy,
  Pencil,
  Check,
  Globe2,
  Compass,
} from "lucide-react";
import { useSession } from "@hostflow/auth/src/use-session";
import { useDiscoverProperties } from "@hostflow/api-client/src/hooks/use-public-properties";
import { demoAvatarUrl } from "@/lib/demo-data";
import { useDemoMyTrips, useDemoMyNotifications } from "@/lib/demo-hooks";
import { useSavedProperties } from "@/lib/use-saved-properties";
import { BrandBackground } from "@/components/nazilco/layout/brand-background";
import { PropertyGrid } from "@/components/nazilco/discover/property-grid";

function StatCard({
  icon: Icon,
  label,
  value,
  href,
}: {
  icon: typeof Heart;
  label: string;
  value: number | string;
  href: string;
}) {
  return (
    <Link href={href}>
      <motion.div
        whileHover={{ y: -3 }}
        transition={{ type: "spring", stiffness: 300, damping: 22 }}
        className="flex h-full flex-col justify-between rounded-2xl border border-border/60 bg-background/70 p-4 shadow-sm backdrop-blur-sm"
      >
        <Icon className="h-4 w-4 text-sapphire-600" />
        <div>
          <p className="text-2xl font-semibold">{value}</p>
          <p className="text-xs text-muted-foreground">{label}</p>
        </div>
      </motion.div>
    </Link>
  );
}

function QuickLink({
  icon: Icon,
  label,
  description,
  href,
}: {
  icon: typeof Heart;
  label: string;
  description: string;
  href: string;
}) {
  return (
    <Link href={href} className="group block">
      <motion.div
        whileHover={{ x: 4 }}
        transition={{ type: "spring", stiffness: 300, damping: 24 }}
        className="flex items-center gap-3 rounded-xl border border-border/60 bg-background/70 p-3.5 shadow-sm transition-colors group-hover:border-sapphire-300"
      >
        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-sapphire-50 text-sapphire-600">
          <Icon className="h-4.5 w-4.5" />
        </div>
        <div className="min-w-0 flex-1">
          <p className="text-sm font-medium">{label}</p>
          <p className="truncate text-xs text-muted-foreground">{description}</p>
        </div>
      </motion.div>
    </Link>
  );
}

export default function ProfilePage() {
  const { user } = useSession();
  const { upcoming } = useDemoMyTrips();
  const { data: notifications } = useDemoMyNotifications();
  const { savedIds } = useSavedProperties();
  const { data: catalog } = useDiscoverProperties(50);

  const [editingName, setEditingName] = useState(false);
  const [displayName, setDisplayName] = useState(user?.name ?? "");
  // No backend endpoint exists yet for guest notification preferences, so
  // these persist to localStorage only (device-local) rather than silently
  // discarding the toggle state on reload, which would be more misleading.
  const [notifyEmail, setNotifyEmail] = useState(true);
  const [notifyPush, setNotifyPush] = useState(true);

  useEffect(() => {
    setNotifyEmail(window.localStorage.getItem("nazilco:notify-email") !== "false");
    setNotifyPush(window.localStorage.getItem("nazilco:notify-push") !== "false");
    const savedName = window.localStorage.getItem("nazilco:display-name");
    if (savedName) setDisplayName(savedName);
  }, []);
  useEffect(() => {
    window.localStorage.setItem("nazilco:notify-email", String(notifyEmail));
  }, [notifyEmail]);
  useEffect(() => {
    window.localStorage.setItem("nazilco:notify-push", String(notifyPush));
  }, [notifyPush]);

  const recentCount = (notifications ?? []).filter(
    (n) => Date.now() - new Date(n.createdAt).getTime() < 1000 * 60 * 60 * 48,
  ).length;
  const savedProperties = (catalog ?? []).filter((p) => savedIds.includes(p.id));

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-4xl p-6">
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <PageHeader title="My Profile" description="Manage your account, trips, and saved stays" />
        </motion.div>

        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: 0.05 }}>
          <Card className="overflow-hidden p-0">
            <div className="h-20 bg-gradient-to-r from-sapphire-600 via-sapphire-500 to-purple-500" />
            <div className="flex flex-col gap-4 p-6 pt-0 sm:flex-row sm:items-end sm:justify-between">
              <div className="-mt-10 flex items-end gap-4">
                <img
                  src={demoAvatarUrl(user?.name ?? user?.email ?? "guest")}
                  alt=""
                  className="h-20 w-20 rounded-full border-4 border-background bg-muted shadow-md"
                />
                <div className="pb-1">
                  {editingName ? (
                    <div className="flex items-center gap-2">
                      <input
                        value={displayName}
                        onChange={(e) => setDisplayName(e.target.value)}
                        className="rounded-full border border-border bg-background px-3 py-1 text-sm font-medium outline-none focus:border-primary"
                        autoFocus
                      />
                      <button
                        type="button"
                        onClick={() => {
                          window.localStorage.setItem("nazilco:display-name", displayName);
                          setEditingName(false);
                        }}
                        aria-label="Save name"
                        className="rounded-full bg-sapphire-600 p-1.5 text-white hover:bg-sapphire-700"
                      >
                        <Check className="h-3.5 w-3.5" />
                      </button>
                    </div>
                  ) : (
                    <button
                      type="button"
                      onClick={() => setEditingName(true)}
                      className="group flex items-center gap-1.5 font-medium"
                    >
                      {displayName || user?.name || "Guest"}
                      <Pencil className="h-3 w-3 text-muted-foreground opacity-0 transition-opacity group-hover:opacity-100" />
                    </button>
                  )}
                  <p className="text-sm text-muted-foreground">{user?.email}</p>
                </div>
              </div>
              <div className="flex items-center gap-1.5 self-start rounded-full bg-sapphire-50 px-3 py-1.5 text-xs font-medium text-sapphire-700 sm:self-auto">
                <Globe2 className="h-3.5 w-3.5" />
                Prices shown in KSh
              </div>
            </div>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="grid grid-cols-2 gap-3 sm:grid-cols-4"
        >
          <StatCard icon={Briefcase} label="Upcoming trips" value={upcoming.length} href="/nazilco/guest-portal" />
          <StatCard icon={Heart} label="Saved stays" value={savedIds.length} href="#saved" />
          <StatCard icon={Bell} label="Recent updates" value={recentCount} href="/nazilco/notifications" />
          <StatCard icon={Receipt} label="Invoices" value="View" href="/nazilco/invoices" />
        </motion.div>

        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: 0.15 }}>
          <Stack gap="sm">
            <h3 className="text-sm font-medium text-muted-foreground">Quick links</h3>
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <QuickLink icon={Briefcase} label="My Trips" description="Upcoming and past bookings" href="/nazilco/guest-portal" />
              <QuickLink icon={Receipt} label="Invoices" description="Payment history and receipts" href="/nazilco/invoices" />
              <QuickLink icon={Bell} label="Notifications" description="Booking and account updates" href="/nazilco/notifications" />
              <QuickLink icon={Compass} label="Discover more stays" description="Browse properties near you" href="/nazilco/discover" />
              <QuickLink icon={LifeBuoy} label="Support" description="Get help with a booking" href="/nazilco/support" />
            </div>
          </Stack>
        </motion.div>

        <motion.div
          id="saved"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.2 }}
        >
          <Stack gap="sm">
            <div className="flex items-center justify-between">
              <h3 className="flex items-center gap-1.5 text-sm font-medium text-muted-foreground">
                <Heart className="h-3.5 w-3.5" /> Saved stays
              </h3>
              {savedProperties.length > 0 && (
                <Link href="/nazilco/discover" className="text-xs font-medium text-sapphire-600 hover:underline">
                  Browse more
                </Link>
              )}
            </div>
            {savedProperties.length === 0 ? (
              <Card className="p-6 text-center">
                <p className="text-sm text-muted-foreground">
                  You haven&rsquo;t saved any stays yet. Tap the heart icon on any property to save it here.
                </p>
                <Link
                  href="/nazilco/discover"
                  className="mt-3 inline-block rounded-full bg-sapphire-600 px-4 py-1.5 text-xs font-medium text-white hover:bg-sapphire-700"
                >
                  Discover stays
                </Link>
              </Card>
            ) : (
              <PropertyGrid properties={savedProperties} />
            )}
          </Stack>
        </motion.div>

        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: 0.25 }}>
          <Stack gap="sm">
            <h3 className="text-sm font-medium text-muted-foreground">Preferences</h3>
            <Card className="divide-y divide-border/60 p-0">
              <label className="flex cursor-pointer items-center justify-between gap-4 p-4">
                <div>
                  <p className="text-sm font-medium">Email notifications</p>
                  <p className="text-xs text-muted-foreground">Booking confirmations, reminders, receipts</p>
                </div>
                <button
                  type="button"
                  role="switch"
                  aria-checked={notifyEmail}
                  onClick={() => setNotifyEmail((v) => !v)}
                  className={`relative h-6 w-11 shrink-0 rounded-full transition-colors ${
                    notifyEmail ? "bg-sapphire-600" : "bg-muted"
                  }`}
                >
                  <motion.span
                    layout
                    transition={{ type: "spring", stiffness: 500, damping: 30 }}
                    className="absolute top-0.5 h-5 w-5 rounded-full bg-white shadow"
                    style={{ left: notifyEmail ? "22px" : "2px" }}
                  />
                </button>
              </label>
              <label className="flex cursor-pointer items-center justify-between gap-4 p-4">
                <div>
                  <p className="text-sm font-medium">Push notifications</p>
                  <p className="text-xs text-muted-foreground">Real-time alerts on this device</p>
                </div>
                <button
                  type="button"
                  role="switch"
                  aria-checked={notifyPush}
                  onClick={() => setNotifyPush((v) => !v)}
                  className={`relative h-6 w-11 shrink-0 rounded-full transition-colors ${
                    notifyPush ? "bg-sapphire-600" : "bg-muted"
                  }`}
                >
                  <motion.span
                    layout
                    transition={{ type: "spring", stiffness: 500, damping: 30 }}
                    className="absolute top-0.5 h-5 w-5 rounded-full bg-white shadow"
                    style={{ left: notifyPush ? "22px" : "2px" }}
                  />
                </button>
              </label>
            </Card>
          </Stack>
        </motion.div>
      </Stack>
    </>
  );
}
