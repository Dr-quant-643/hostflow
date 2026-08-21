"use client";

import { motion, AnimatePresence } from "framer-motion";
import { EmptyState } from "@hostflow/ui";
import { useDiscoverProperties } from "@hostflow/api-client/src/hooks/use-public-properties";
import type { PublicPropertySummary } from "@hostflow/types";
import type { Coordinates } from "@/lib/geo";
import { PropertyCard } from "./property-card";

type GridProperty = PublicPropertySummary & { distanceKm?: number };

const SKELETON_HEIGHTS = ["h-56", "h-72", "h-64", "h-80", "h-60", "h-96", "h-64", "h-52", "h-72"];

export function PropertyGrid({
  properties,
  userLocation,
}: {
  properties?: GridProperty[];
  userLocation?: Coordinates | null;
} = {}) {
  const query = useDiscoverProperties();
  const isLoading = properties === undefined && query.isLoading;
  const isError = properties === undefined && query.isError;
  const data: GridProperty[] | undefined = properties ?? query.data;

  if (isLoading) {
    return (
      <div className="columns-2 gap-4 sm:columns-3 lg:columns-4 xl:columns-5">
        {SKELETON_HEIGHTS.map((h, i) => (
          <div
            key={i}
            className={`mb-4 animate-pulse break-inside-avoid overflow-hidden rounded-3xl bg-muted ${h}`}
          />
        ))}
      </div>
    );
  }
  if (isError) {
    return <EmptyState title="Couldn't load properties" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No stays match your filters"
        description="Try widening your price range or clearing a filter."
      />
    );
  }

  return (
    <AnimatePresence mode="popLayout">
      <div className="columns-2 gap-4 sm:columns-3 lg:columns-4 xl:columns-5">
        {data.map((property, i) => (
          <motion.div
            key={property.id}
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.96 }}
            transition={{ duration: 0.35, delay: i * 0.03 }}
            className="mb-4 break-inside-avoid"
          >
            <PropertyCard
              property={property}
              distanceKm={property.distanceKm}
              userLocation={userLocation}
            />
          </motion.div>
        ))}
      </div>
    </AnimatePresence>
  );
}
