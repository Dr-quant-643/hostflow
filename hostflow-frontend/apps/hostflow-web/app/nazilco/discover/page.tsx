"use client";

import { Suspense, useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import { motion } from "framer-motion";
import { Heart } from "lucide-react";
import { Stack, PageHeader } from "@hostflow/ui";
import { useDiscoverProperties } from "@hostflow/api-client/src/hooks/use-public-properties";
import type { PublicPropertySummary } from "@hostflow/types";
import { useGeolocation } from "@/lib/use-geolocation";
import { distanceKm as computeDistanceKm } from "@/lib/geo";
import { useSavedProperties } from "@/lib/use-saved-properties";
import { PropertyGrid } from "@/components/nazilco/discover/property-grid";
import { BrandBackground } from "@/components/nazilco/layout/brand-background";
import {
  DiscoverFilters,
  DEFAULT_FILTERS,
  type DiscoverFilterState,
} from "@/components/nazilco/discover/discover-filters";

type PropertyWithDistance = PublicPropertySummary & {
  distanceKm?: number;
  maxGuests?: number;
  rating?: number;
};

function applyFilters(
  properties: PropertyWithDistance[],
  filters: DiscoverFilterState,
): PropertyWithDistance[] {
  let result = properties;

  const q = filters.query.trim().toLowerCase();
  if (q) {
    result = result.filter(
      (p) =>
        p.name.toLowerCase().includes(q) ||
        p.city.toLowerCase().includes(q) ||
        p.country.toLowerCase().includes(q),
    );
  }
  if (filters.propertyType) {
    result = result.filter((p) => p.propertyType === filters.propertyType);
  }
  if (filters.rentalModel) {
    result = result.filter((p) => p.rentalModel === filters.rentalModel);
  }
  if (filters.city) {
    result = result.filter((p) => p.city === filters.city);
  }
  if (filters.minPrice) {
    const min = Number(filters.minPrice);
    result = result.filter((p) => p.basePrice != null && Number(p.basePrice) >= min);
  }
  if (filters.maxPrice) {
    const max = Number(filters.maxPrice);
    result = result.filter((p) => p.basePrice != null && Number(p.basePrice) <= max);
  }
  if (filters.minGuests) {
    const min = Number(filters.minGuests);
    result = result.filter((p) => p.maxGuests == null || p.maxGuests >= min);
  }

  result = [...result];
  switch (filters.sort) {
    case "price-asc":
      result.sort((a, b) => Number(a.basePrice ?? 0) - Number(b.basePrice ?? 0));
      break;
    case "price-desc":
      result.sort((a, b) => Number(b.basePrice ?? 0) - Number(a.basePrice ?? 0));
      break;
    case "rating-desc":
      result.sort((a, b) => (b.rating ?? 0) - (a.rating ?? 0));
      break;
    case "name-asc":
      result.sort((a, b) => a.name.localeCompare(b.name));
      break;
    case "nearest":
      result.sort((a, b) => (a.distanceKm ?? Infinity) - (b.distanceKm ?? Infinity));
      break;
  }

  return result;
}

export default function DiscoverPage() {
  return (
    <Suspense fallback={null}>
      <DiscoverPageContent />
    </Suspense>
  );
}

// useSearchParams() (used for the ?nearMe=1 deep link) requires a Suspense
// boundary during static generation, or `next build` fails prerendering
// this route entirely -- `next dev` doesn't enforce this, which is why it
// went unnoticed until a real production build actually ran.
function DiscoverPageContent() {
  const { data } = useDiscoverProperties();
  const [filters, setFilters] = useState<DiscoverFilterState>(DEFAULT_FILTERS);
  const [savedOnly, setSavedOnly] = useState(false);
  const geo = useGeolocation();
  const { savedIds } = useSavedProperties();
  const searchParams = useSearchParams();
  const autoRequestedRef = useRef(false);

  // Arrived via the homepage's "Show stays near me" or category-pill links
  // — kick off the location prompt / seed the type filter automatically
  // instead of making the user re-apply what they already chose.
  useEffect(() => {
    if (autoRequestedRef.current) return;
    const nearMe = searchParams.get("nearMe") === "1";
    const type = searchParams.get("type");
    if (!nearMe && !type) return;
    autoRequestedRef.current = true;
    if (nearMe) geo.request();
    setFilters((f) => ({
      ...f,
      sort: nearMe ? "nearest" : f.sort,
      propertyType: type ?? f.propertyType,
    }));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  const withDistance = useMemo<PropertyWithDistance[]>(() => {
    if (!data) return [];
    if (!geo.coords) return data;
    return data.map((p) => ({
      ...p,
      distanceKm:
        p.latitude != null && p.longitude != null
          ? computeDistanceKm(geo.coords!, { latitude: p.latitude, longitude: p.longitude })
          : undefined,
    }));
  }, [data, geo.coords]);

  const propertyTypes = useMemo(
    () => Array.from(new Set((data ?? []).map((p) => p.propertyType))).sort(),
    [data],
  );
  const cities = useMemo(
    () => Array.from(new Set((data ?? []).map((p) => p.city))).sort(),
    [data],
  );
  const filtered = useMemo(() => {
    const base = applyFilters(withDistance, filters);
    return savedOnly ? base.filter((p) => savedIds.includes(p.id)) : base;
  }, [withDistance, filters, savedOnly, savedIds]);

  const handleRequestLocation = () => {
    geo.request();
    if (filters.sort === "recommended") {
      setFilters((f) => ({ ...f, sort: "nearest" }));
    }
  };

  const handleClearLocation = () => {
    geo.clear();
    if (filters.sort === "nearest") {
      setFilters((f) => ({ ...f, sort: "recommended" }));
    }
  };

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-6xl p-6">
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <PageHeader
            title="Explore stays"
            description="Browse properties the way you browse ideas — save what you love."
          />
        </motion.div>

        <DiscoverFilters
          filters={filters}
          onChange={setFilters}
          propertyTypes={propertyTypes}
          cities={cities}
          resultCount={filtered.length}
          locationStatus={geo.status}
          onRequestLocation={handleRequestLocation}
          onClearLocation={handleClearLocation}
        />

        <div className="flex flex-wrap items-center justify-between gap-2">
          <button
            type="button"
            onClick={() => setSavedOnly((s) => !s)}
            className={`flex items-center gap-1.5 rounded-full border px-3 py-1.5 text-xs font-medium transition-colors ${
              savedOnly
                ? "border-red-300 bg-red-50 text-red-600"
                : "border-border text-muted-foreground hover:bg-muted"
            }`}
          >
            <Heart className={`h-3.5 w-3.5 ${savedOnly ? "fill-red-500 text-red-500" : ""}`} />
            {savedOnly ? "Showing saved" : "Saved only"}
            {savedIds.length > 0 && (
              <span className="rounded-full bg-black/10 px-1.5 text-[10px] font-semibold">
                {savedIds.length}
              </span>
            )}
          </button>
        </div>

        <PropertyGrid properties={data ? filtered : undefined} userLocation={geo.coords} />
      </Stack>
    </>
  );
}
