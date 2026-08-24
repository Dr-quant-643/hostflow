"use client";

import { Search, SlidersHorizontal, X, LocateFixed, Loader2, MapPinOff } from "lucide-react";
import { motion } from "framer-motion";
import type { GeolocationStatus } from "@/lib/use-geolocation";

export interface DiscoverFilterState {
  query: string;
  propertyType: string;
  rentalModel: "" | "NIGHTLY" | "MONTHLY";
  city: string;
  minPrice: string;
  maxPrice: string;
  minGuests: string;
  sort: "recommended" | "price-asc" | "price-desc" | "rating-desc" | "name-asc" | "nearest";
}

export const DEFAULT_FILTERS: DiscoverFilterState = {
  query: "",
  propertyType: "",
  rentalModel: "",
  city: "",
  minPrice: "",
  maxPrice: "",
  minGuests: "",
  sort: "recommended",
};

const PROPERTY_TYPE_LABELS: Record<string, string> = {
  VACATION_RENTAL: "Vacation rental",
  RESIDENTIAL: "Apartment",
  HOTEL: "Hotel",
  OFFICE: "Office",
  RETAIL_MALL: "Retail",
  MIXED_USE: "Mixed use",
};

const SORT_LABELS: Record<DiscoverFilterState["sort"], string> = {
  recommended: "Recommended",
  nearest: "Nearest to me",
  "price-asc": "Price: low to high",
  "price-desc": "Price: high to low",
  "rating-desc": "Top rated",
  "name-asc": "Name A–Z",
};

function selectClass() {
  return "rounded-full border border-border bg-background px-3 py-2 text-sm outline-none transition-colors focus:border-primary";
}

export function DiscoverFilters({
  filters,
  onChange,
  propertyTypes,
  cities,
  resultCount,
  locationStatus,
  onRequestLocation,
  onClearLocation,
}: {
  filters: DiscoverFilterState;
  onChange: (next: DiscoverFilterState) => void;
  propertyTypes: string[];
  cities: string[];
  resultCount: number;
  locationStatus: GeolocationStatus;
  onRequestLocation: () => void;
  onClearLocation: () => void;
}) {
  const isDirty = JSON.stringify(filters) !== JSON.stringify(DEFAULT_FILTERS);
  const hasLocation = locationStatus === "granted";

  const set = <K extends keyof DiscoverFilterState>(key: K, value: DiscoverFilterState[K]) =>
    onChange({ ...filters, [key]: value });

  return (
    <motion.div
      initial={{ opacity: 0, y: -8 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35 }}
      className="space-y-3 rounded-2xl border border-border/60 bg-background/70 p-4 shadow-sm backdrop-blur-md"
    >
      <div className="flex flex-wrap items-center gap-2">
        <label className="flex min-w-[200px] flex-1 items-center gap-2 rounded-full border border-border bg-background px-3 py-2">
          <Search className="h-4 w-4 shrink-0 text-muted-foreground" />
          <input
            value={filters.query}
            onChange={(e) => set("query", e.target.value)}
            placeholder="Search by name or city..."
            className="w-full bg-transparent text-sm outline-none placeholder:text-muted-foreground/70"
          />
        </label>

        <select
          value={filters.propertyType}
          onChange={(e) => set("propertyType", e.target.value)}
          className={selectClass()}
        >
          <option value="">All property types</option>
          {propertyTypes.map((t) => (
            <option key={t} value={t}>
              {PROPERTY_TYPE_LABELS[t] ?? t}
            </option>
          ))}
        </select>

        <select
          value={filters.rentalModel}
          onChange={(e) => set("rentalModel", e.target.value as DiscoverFilterState["rentalModel"])}
          className={selectClass()}
        >
          <option value="">Short stays &amp; long-term</option>
          <option value="NIGHTLY">Short stays only</option>
          <option value="MONTHLY">Long-term rentals only</option>
        </select>

        <select
          value={filters.city}
          onChange={(e) => set("city", e.target.value)}
          className={selectClass()}
        >
          <option value="">All locations</option>
          {cities.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>

        <select
          value={filters.sort}
          onChange={(e) => set("sort", e.target.value as DiscoverFilterState["sort"])}
          className={selectClass()}
        >
          {Object.entries(SORT_LABELS).map(([value, label]) => {
            if (value === "nearest" && !hasLocation) return null;
            return (
              <option key={value} value={value}>
                {label}
              </option>
            );
          })}
        </select>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        <span className="flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
          <SlidersHorizontal className="h-3.5 w-3.5" />
          Price (KSh)
        </span>
        <input
          type="number"
          min={0}
          value={filters.minPrice}
          onChange={(e) => set("minPrice", e.target.value)}
          placeholder="Min"
          className="w-24 rounded-full border border-border bg-background px-3 py-1.5 text-sm outline-none focus:border-primary"
        />
        <span className="text-muted-foreground">–</span>
        <input
          type="number"
          min={0}
          value={filters.maxPrice}
          onChange={(e) => set("maxPrice", e.target.value)}
          placeholder="Max"
          className="w-24 rounded-full border border-border bg-background px-3 py-1.5 text-sm outline-none focus:border-primary"
        />

        <span className="ml-2 text-xs font-medium text-muted-foreground">Guests</span>
        <input
          type="number"
          min={1}
          value={filters.minGuests}
          onChange={(e) => set("minGuests", e.target.value)}
          placeholder="Any"
          className="w-16 rounded-full border border-border bg-background px-3 py-1.5 text-sm outline-none focus:border-primary"
        />

        {hasLocation ? (
          <button
            type="button"
            onClick={onClearLocation}
            className="flex items-center gap-1.5 rounded-full border border-primary/30 bg-primary/10 px-3 py-1.5 text-xs font-medium text-primary hover:bg-primary/15"
          >
            <LocateFixed className="h-3.5 w-3.5" />
            Using your location
            <X className="h-3 w-3" />
          </button>
        ) : (
          <button
            type="button"
            onClick={onRequestLocation}
            disabled={locationStatus === "requesting"}
            className="flex items-center gap-1.5 rounded-full border border-border px-3 py-1.5 text-xs font-medium hover:bg-muted disabled:opacity-60"
          >
            {locationStatus === "requesting" ? (
              <Loader2 className="h-3.5 w-3.5 animate-spin" />
            ) : locationStatus === "denied" || locationStatus === "unsupported" ? (
              <MapPinOff className="h-3.5 w-3.5" />
            ) : (
              <LocateFixed className="h-3.5 w-3.5" />
            )}
            {locationStatus === "denied"
              ? "Location unavailable"
              : locationStatus === "unsupported"
                ? "Location not supported"
                : "Near me"}
          </button>
        )}

        {isDirty && (
          <button
            type="button"
            onClick={() => onChange(DEFAULT_FILTERS)}
            className="flex items-center gap-1 rounded-full border border-border px-3 py-1.5 text-xs font-medium text-muted-foreground hover:bg-muted"
          >
            <X className="h-3.5 w-3.5" />
            Clear filters
          </button>
        )}

        <span className="ml-auto text-sm text-muted-foreground">
          {resultCount} listing{resultCount === 1 ? "" : "s"}
        </span>
      </div>
    </motion.div>
  );
}
