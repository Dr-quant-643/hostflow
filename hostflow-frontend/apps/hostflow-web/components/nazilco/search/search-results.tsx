"use client";

import { useMemo, useState } from "react";
import { useSearchParams } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import { EmptyState } from "@hostflow/ui";
import {
  useSearchProperties,
  type PropertySearchParams,
} from "@hostflow/api-client/src/hooks/use-public-properties";
import { useGeolocation } from "@/lib/use-geolocation";
import { distanceKm as computeDistanceKm } from "@/lib/geo";
import { PropertyCard } from "@/components/nazilco/discover/property-card";

type SortOption = "relevance" | "price-asc" | "price-desc" | "name-asc" | "nearest";

const SORT_LABELS: Record<SortOption, string> = {
  relevance: "Relevance",
  "price-asc": "Price: low to high",
  "price-desc": "Price: high to low",
  "name-asc": "Name A–Z",
  nearest: "Nearest to me",
};

export function SearchResults() {
  const searchParams = useSearchParams();
  const [sort, setSort] = useState<SortOption>("relevance");
  const geo = useGeolocation();

  const checkIn = searchParams.get("checkIn");
  const checkOut = searchParams.get("checkOut");
  const guests = searchParams.get("guests");
  const destination = searchParams.get("destination") ?? undefined;

  const params: PropertySearchParams | null =
    checkIn && checkOut ? { city: destination } : null;

  const { data, isLoading, isError } = useSearchProperties(params);

  const sorted = useMemo(() => {
    if (!data) return undefined;
    const withDistance = geo.coords
      ? data.map((p) => ({
          ...p,
          distanceKm:
            p.latitude != null && p.longitude != null
              ? computeDistanceKm(geo.coords!, { latitude: p.latitude, longitude: p.longitude })
              : undefined,
        }))
      : data;

    const list = [...withDistance];
    switch (sort) {
      case "price-asc":
        list.sort((a, b) => Number(a.basePrice ?? 0) - Number(b.basePrice ?? 0));
        break;
      case "price-desc":
        list.sort((a, b) => Number(b.basePrice ?? 0) - Number(a.basePrice ?? 0));
        break;
      case "name-asc":
        list.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case "nearest":
        list.sort(
          (a, b) =>
            ((a as { distanceKm?: number }).distanceKm ?? Infinity) -
            ((b as { distanceKm?: number }).distanceKm ?? Infinity),
        );
        break;
    }
    return list;
  }, [data, sort, geo.coords]);

  if (!params) {
    return (
      <EmptyState
        title="Enter your dates to search"
        description="Pick a destination, check-in, and check-out date above to see available stays."
      />
    );
  }
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="animate-pulse overflow-hidden rounded-2xl border border-border/60">
            <div className="aspect-[4/3] bg-muted" />
            <div className="space-y-2 p-4">
              <div className="h-4 w-2/3 rounded bg-muted" />
              <div className="h-3 w-1/2 rounded bg-muted" />
            </div>
          </div>
        ))}
      </div>
    );
  }
  if (isError || !sorted || sorted.length === 0) {
    return (
      <EmptyState
        title="No properties match your search"
        description="Try different dates or a broader destination."
      />
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <p className="text-sm text-muted-foreground">
          {sorted.length} stay{sorted.length === 1 ? "" : "s"}
          {destination ? (
            <>
              {" "}
              in <span className="font-medium text-foreground">{destination}</span>
            </>
          ) : null}
          {checkIn && checkOut && (
            <>
              {" "}
              · {checkIn} → {checkOut}
            </>
          )}
          {guests && <> · {guests} guest{guests === "1" ? "" : "s"}</>}
        </p>

        <div className="flex items-center gap-2">
          {geo.status !== "granted" ? (
            <button
              type="button"
              onClick={geo.request}
              disabled={geo.status === "requesting"}
              className="rounded-full border border-border px-3 py-1.5 text-xs font-medium text-muted-foreground hover:bg-muted disabled:opacity-60"
            >
              {geo.status === "requesting" ? "Locating…" : "Sort by distance"}
            </button>
          ) : null}
          <select
            value={sort}
            onChange={(e) => setSort(e.target.value as SortOption)}
            className="rounded-full border border-border bg-background px-3 py-1.5 text-xs font-medium outline-none focus:border-primary"
          >
            {Object.entries(SORT_LABELS).map(([value, label]) => {
              if (value === "nearest" && geo.status !== "granted") return null;
              return (
                <option key={value} value={value}>
                  {label}
                </option>
              );
            })}
          </select>
        </div>
      </div>

      <AnimatePresence mode="popLayout">
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {sorted.map((property, i) => (
            <motion.div
              key={property.id}
              layout
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.35, delay: i * 0.05 }}
            >
              <PropertyCard
                property={property}
                distanceKm={(property as { distanceKm?: number }).distanceKm}
                userLocation={geo.coords}
              />
            </motion.div>
          ))}
        </div>
      </AnimatePresence>
    </div>
  );
}
