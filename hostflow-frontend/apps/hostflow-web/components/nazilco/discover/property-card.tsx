"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Star, Heart, MapPin, Navigation } from "lucide-react";
import type { PublicPropertySummary } from "@hostflow/types";
import { usePropertyPhotos } from "@hostflow/api-client/src/hooks/use-public-properties";
import { formatKES } from "@/lib/currency";
import {
  formatDistance,
  estimateDriveMinutes,
  formatDriveTime,
  externalDirectionsUrl,
  type Coordinates,
} from "@/lib/geo";
import { useSavedProperties } from "@/lib/use-saved-properties";

type CardProperty = PublicPropertySummary & {
  photos?: string[];
  rating?: number;
  reviewCount?: number;
};

const FALLBACK_IMAGE =
  "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1200&q=80";

export function PropertyCard({
  property,
  distanceKm,
  userLocation,
}: {
  property: CardProperty;
  distanceKm?: number;
  userLocation?: Coordinates | null;
}) {
  const { isSaved, toggle } = useSavedProperties();
  const saved = isSaved(property.id);
  // Real property photos are a separate endpoint (PublicPropertySummary
  // carries no photos field) — grid cards previously only showed a photo
  // when a parent explicitly passed one in, which never happened on the
  // discover grid, so every card silently showed the same generic stock
  // fallback image regardless of the property's actual photos. Fetching
  // here makes the discover dashboard an actual photo preview of each
  // listing; `enabled: !propertyPhotos` skips the request entirely when a
  // parent already supplied photos (e.g. saved-properties reuse).
  const { data: fetchedPhotos } = usePropertyPhotos(property.photos ? "" : property.id);
  const photos = property.photos ?? fetchedPhotos;
  const image = photos?.[0] ?? FALLBACK_IMAGE;

  return (
    <Link href={`/properties/${property.id}`} className="group block">
      <motion.div
        whileHover={{ y: -4 }}
        transition={{ type: "spring", stiffness: 300, damping: 22 }}
        className="overflow-hidden rounded-2xl border border-border/60 bg-card shadow-sm transition-shadow group-hover:shadow-lg"
      >
        <div className="relative aspect-[4/3] overflow-hidden">
          <motion.img
            src={image}
            alt={property.name}
            className="h-full w-full object-cover"
            whileHover={{ scale: 1.08 }}
            transition={{ duration: 0.5, ease: "easeOut" }}
          />
          <button
            type="button"
            onClick={(e) => {
              e.preventDefault();
              toggle(property.id);
            }}
            aria-label={saved ? "Remove from saved" : "Save"}
            className="absolute right-3 top-3 rounded-full bg-black/25 p-1.5 backdrop-blur-sm transition-colors hover:bg-black/40"
          >
            <motion.span
              key={saved ? "saved" : "unsaved"}
              initial={{ scale: 0.6 }}
              animate={{ scale: 1 }}
              transition={{ type: "spring", stiffness: 400, damping: 15 }}
              className="block"
            >
              <Heart
                className="h-4 w-4 transition-colors"
                fill={saved ? "#ef4444" : "transparent"}
                color={saved ? "#ef4444" : "white"}
              />
            </motion.span>
          </button>
          {property.rating != null && (
            <div className="absolute bottom-3 left-3 flex items-center gap-1 rounded-full bg-white/95 px-2 py-0.5 text-xs font-medium shadow-sm">
              <Star className="h-3 w-3 fill-amber-400 text-amber-400" />
              {property.rating.toFixed(2)}
            </div>
          )}
        </div>

        <div className="space-y-1 p-4">
          <div className="flex items-start justify-between gap-2">
            <h3 className="line-clamp-1 font-medium">{property.name}</h3>
          </div>
          <p className="line-clamp-1 text-sm text-muted-foreground">
            {property.city}, {property.country}
          </p>
          {distanceKm != null && (
            <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs font-medium text-sapphire-600">
              <span className="flex items-center gap-1">
                <MapPin className="h-3 w-3" />
                {formatDistance(distanceKm)}
              </span>
              <span className="text-muted-foreground">
                {formatDriveTime(estimateDriveMinutes(distanceKm))}
              </span>
              {userLocation && property.latitude != null && property.longitude != null && (
                <a
                  href={externalDirectionsUrl(userLocation, {
                    latitude: property.latitude,
                    longitude: property.longitude,
                  })}
                  target="_blank"
                  rel="noopener noreferrer"
                  onClick={(e) => e.stopPropagation()}
                  className="flex items-center gap-1 text-sapphire-700 underline-offset-2 hover:underline"
                >
                  <Navigation className="h-3 w-3" />
                  Directions
                </a>
              )}
            </div>
          )}
          <p className="pt-1 text-sm">
            {property.basePrice ? (
              <>
                <span className="font-semibold">{formatKES(property.basePrice)}</span>{" "}
                <span className="text-muted-foreground">/ night</span>
              </>
            ) : (
              <span className="text-muted-foreground">Price on request</span>
            )}
          </p>
        </div>
      </motion.div>
    </Link>
  );
}
