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

// Masonry needs varied tile heights to read as a mosaic rather than a grid.
// Real photos already vary naturally (no forced aspect ratio below); this
// only kicks in for the generic fallback image so it doesn't tile as a wall
// of identical squares.
const FALLBACK_RATIOS = ["aspect-[3/4]", "aspect-[4/5]", "aspect-square", "aspect-[4/3]", "aspect-[9/16]"];
function fallbackRatio(id: string) {
  let hash = 0;
  for (let i = 0; i < id.length; i++) hash = (hash * 31 + id.charCodeAt(i)) >>> 0;
  return FALLBACK_RATIOS[hash % FALLBACK_RATIOS.length];
}

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
  const hasRealPhoto = Boolean(photos?.[0]);
  const image = photos?.[0] ?? FALLBACK_IMAGE;

  return (
    <Link href={`/nazilco/properties/${property.id}`} className="group block">
      <motion.div
        whileHover={{ y: -6 }}
        transition={{ type: "spring", stiffness: 300, damping: 22 }}
        className="relative overflow-hidden rounded-3xl bg-card shadow-sm ring-1 ring-border/50 transition-shadow group-hover:shadow-2xl group-hover:shadow-purple-500/10"
      >
        <div className={`relative overflow-hidden ${hasRealPhoto ? "" : fallbackRatio(property.id)}`}>
          <motion.img
            src={image}
            alt={property.name}
            className={`block w-full ${hasRealPhoto ? "h-auto" : "h-full object-cover"}`}
            whileHover={{ scale: 1.05 }}
            transition={{ duration: 0.5, ease: "easeOut" }}
          />

          <div className="pointer-events-none absolute inset-x-0 top-0 h-16 bg-gradient-to-b from-black/40 to-transparent" />

          <button
            type="button"
            onClick={(e) => {
              e.preventDefault();
              toggle(property.id);
            }}
            aria-label={saved ? "Remove from saved" : "Save"}
            className="absolute right-3 top-3 rounded-full bg-black/30 p-2 backdrop-blur-md transition-all hover:scale-110 hover:bg-black/50"
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
            <div className="absolute left-3 top-3 flex items-center gap-1 rounded-full bg-white/95 px-2 py-0.5 text-xs font-medium shadow-sm">
              <Star className="h-3 w-3 fill-amber-400 text-amber-400" />
              {property.rating.toFixed(2)}
            </div>
          )}

          {/* Pinterest-style hover reveal over the photo itself */}
          <div className="pointer-events-none absolute inset-x-0 bottom-0 translate-y-2 bg-gradient-to-t from-black/85 via-black/40 to-transparent p-3 pt-8 opacity-0 transition-all duration-300 group-hover:translate-y-0 group-hover:opacity-100">
            <p className="line-clamp-1 text-sm font-semibold text-white">{property.name}</p>
            <p className="line-clamp-1 text-xs text-white/80">
              {property.city}, {property.country}
            </p>
          </div>
        </div>

        <div className="space-y-1.5 p-3.5">
          <h3 className="line-clamp-1 text-sm font-medium text-foreground">{property.name}</h3>
          <p className="line-clamp-1 text-xs text-muted-foreground">
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
          <p className="pt-0.5 text-sm">
            {property.basePrice ? (
              <>
                <span className="font-semibold text-foreground">{formatKES(property.basePrice)}</span>{" "}
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
