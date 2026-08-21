"use client";

import { motion, AnimatePresence } from "framer-motion";
import { EmptyState } from "@hostflow/ui";
import { useDiscoverProperties } from "@hostflow/api-client/src/hooks/use-public-properties";
import type { PublicPropertySummary } from "@hostflow/types";
import type { Coordinates } from "@/lib/geo";
import { PropertyCard } from "./property-card";

type GridProperty = PublicPropertySummary & { distanceKm?: number };

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
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="animate-pulse overflow-hidden rounded-2xl border border-border/60">
            <div className="aspect-[4/3] bg-muted" />
            <div className="space-y-2 p-4">
              <div className="h-4 w-2/3 rounded bg-muted" />
              <div className="h-3 w-1/2 rounded bg-muted" />
              <div className="h-3 w-1/3 rounded bg-muted" />
            </div>
          </div>
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
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {data.map((property, i) => (
          <motion.div
            key={property.id}
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.96 }}
            transition={{ duration: 0.35, delay: i * 0.04 }}
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
