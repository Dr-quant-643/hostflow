"use client";

import { motion } from "framer-motion";
import { PageHeader, Stack, Skeleton, EmptyState } from "@hostflow/ui";
import { Link as LinkIcon } from "lucide-react";
import Link from "next/link";
import { useDemoMyTrips } from "@/lib/demo-hooks";
import { TripCard } from "@/components/nazilco/guest-portal/trip-card";
import { BrandBackground } from "@/components/nazilco/layout/brand-background";

export default function GuestPortalPage() {
  const { upcoming, past, isLoading, isError } = useDemoMyTrips();

  if (isLoading)
    return (
      <>
        <BrandBackground />
        <div className="mx-auto max-w-3xl p-6">
          <Skeleton className="h-96 w-full" />
        </div>
      </>
    );
  if (isError) {
    return (
      <>
        <BrandBackground />
        <div className="mx-auto max-w-3xl p-6">
          <EmptyState title="Couldn't load your trips" description="Try refreshing." />
        </div>
      </>
    );
  }

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-3xl p-6">
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <PageHeader title="My Trips" description="Everything you've booked, past and upcoming" />
        </motion.div>

        <Stack gap="sm">
          <h3 className="font-medium">Upcoming</h3>
          {upcoming.length === 0 ? (
            <EmptyState
              title="No upcoming trips"
              description="Ready for your next getaway?"
              action={
                <Link
                  href="/nazilco/discover"
                  className="mt-2 inline-flex items-center gap-1.5 rounded-full bg-sapphire-600 px-4 py-1.5 text-xs font-medium text-white hover:bg-sapphire-700"
                >
                  <LinkIcon className="h-3.5 w-3.5" /> Browse stays
                </Link>
              }
            />
          ) : (
            <Stack gap="sm">
              {upcoming.map((b, i) => (
                <motion.div
                  key={b.id}
                  initial={{ opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.35, delay: i * 0.06 }}
                >
                  <TripCard booking={b} />
                </motion.div>
              ))}
            </Stack>
          )}
        </Stack>

        <Stack gap="sm">
          <h3 className="font-medium">Past</h3>
          {past.length === 0 ? (
            <EmptyState title="No past trips" />
          ) : (
            <Stack gap="sm">
              {past.map((b, i) => (
                <motion.div
                  key={b.id}
                  initial={{ opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.35, delay: i * 0.06 }}
                >
                  <TripCard booking={b} />
                </motion.div>
              ))}
            </Stack>
          )}
        </Stack>
      </Stack>
    </>
  );
}
