"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Card, Badge, Stack } from "@hostflow/ui";
import { ChevronRight, MapPin, Navigation } from "lucide-react";
import { usePublicProperty } from "@/lib/demo-hooks";
import { formatKES } from "@/lib/currency";
import { externalDirectionsUrlToDestination } from "@/lib/geo";
import type { BookingResponse } from "@hostflow/types";

const STATUS_VARIANT: Record<string, "default" | "success" | "warning" | "destructive" | "outline"> = {
  PENDING: "warning",
  CONFIRMED: "success",
  CHECKED_IN: "success",
  CHECKED_OUT: "outline",
  CANCELLED: "destructive",
};

function nightsBetween(checkIn: string, checkOut: string): number {
  const ms = new Date(checkOut).getTime() - new Date(checkIn).getTime();
  return Math.max(1, Math.round(ms / (1000 * 60 * 60 * 24)));
}

function checkInCountdown(checkIn: string): string | null {
  const days = Math.round((new Date(checkIn).getTime() - Date.now()) / (1000 * 60 * 60 * 24));
  if (days < 0) return null;
  if (days === 0) return "Check-in today";
  if (days === 1) return "Check-in tomorrow";
  return `Check-in in ${days} days`;
}

// BookingResponse has no propertyName — the real DTO only carries
// propertyId, so the display name/photo is looked up per card.
export function TripCard({ booking }: { booking: BookingResponse }) {
  const { data: property } = usePublicProperty(booking.propertyId);
  const countdown = checkInCountdown(booking.checkIn);
  const hasCoords = property && "latitude" in property && property.latitude != null && property.longitude != null;

  return (
    <Card className="overflow-hidden transition-shadow hover:shadow-md">
      <Link href={`/guest-portal/${booking.id}`} className="block">
        <motion.div whileHover={{ y: -2 }} transition={{ type: "spring", stiffness: 300, damping: 22 }}>
          <Stack direction="row" gap="md">
            <div className="relative h-24 w-32 shrink-0 bg-muted">
              {property && "photos" in property && property.photos?.[0] && (
                <img src={property.photos[0]} alt="" className="h-full w-full object-cover" />
              )}
              {countdown && (
                <span className="absolute bottom-1 left-1 rounded-full bg-black/70 px-1.5 py-0.5 text-[10px] font-medium text-white">
                  {countdown}
                </span>
              )}
            </div>
            <Stack gap="sm" className="min-w-0 flex-1 p-4 pl-0">
              <Stack direction="row" gap="sm" align="center">
                <span className="line-clamp-1 font-medium">{property?.name ?? "Property"}</span>
                <Badge variant={STATUS_VARIANT[booking.status] ?? "default"}>{booking.status}</Badge>
              </Stack>
              {property?.city && (
                <span className="flex items-center gap-1 text-xs text-muted-foreground">
                  <MapPin className="h-3 w-3" /> {property.city}, {property.country}
                </span>
              )}
              <span className="text-sm text-muted-foreground">
                {booking.checkIn} → {booking.checkOut} · {nightsBetween(booking.checkIn, booking.checkOut)} nights
              </span>
              <span className="text-sm font-medium">{formatKES(booking.totalPrice)}</span>
            </Stack>
            <div className="flex items-center pr-4 text-muted-foreground">
              <ChevronRight className="h-4 w-4" />
            </div>
          </Stack>
        </motion.div>
      </Link>
      {hasCoords && (
        <div className="border-t border-border/60 px-4 py-2">
          <a
            href={externalDirectionsUrlToDestination({
              latitude: (property as { latitude: number }).latitude,
              longitude: (property as { longitude: number }).longitude,
            })}
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center gap-1.5 text-xs font-medium text-sapphire-600 hover:underline"
          >
            <Navigation className="h-3.5 w-3.5" />
            Get directions
          </a>
        </div>
      )}
    </Card>
  );
}
