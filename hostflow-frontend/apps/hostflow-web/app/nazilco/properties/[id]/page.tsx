"use client";

import { Suspense } from "react";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { motion } from "framer-motion";
import {
  Skeleton,
  EmptyState,
  Stack,
  Card,
  Button,
} from "@hostflow/ui";
import { Star, MapPin, BedDouble, Bath, Users, Sparkles } from "lucide-react";
import {
  usePublicProperty,
  usePropertyPhotos,
} from "@hostflow/api-client/src/hooks/use-public-properties";
import { demoAvatarUrl, type DemoProperty } from "@/lib/demo-data";
import type { PublicPropertySummary } from "@hostflow/types";
import { formatKES } from "@/lib/currency";
import { PhotoGallery } from "@/components/nazilco/property/photo-gallery";
import { AvailabilityCalendar } from "@/components/nazilco/property/availability-calendar";
import { ReviewsSection } from "@/components/nazilco/property/reviews-section";

type Property = PublicPropertySummary & Partial<DemoProperty>;

export default function PropertyDetailPage() {
  return (
    <Suspense fallback={null}>
      <PropertyDetailPageContent />
    </Suspense>
  );
}

// useSearchParams() (used for the checkIn/checkOut trip deep link) requires a
// Suspense boundary during static generation, or `next build` fails
// prerendering this route entirely.
function PropertyDetailPageContent() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const searchParams = useSearchParams();
  const { data, isLoading, isError } = usePublicProperty(id);
  const { data: photoUrls } = usePropertyPhotos(id);
  const property = data as Property | undefined;

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !property) return <EmptyState title="Property not found" />;

  const tripCheckIn = searchParams.get("checkIn");
  const tripCheckOut = searchParams.get("checkOut");

  return (
    <Stack gap="lg" className="mx-auto max-w-6xl p-6">
      <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
        <h1 className="text-2xl font-semibold sm:text-3xl">{property.name}</h1>
        <div className="mt-2 flex flex-wrap items-center gap-3 text-sm text-muted-foreground">
          {property.rating != null && (
            <span className="flex items-center gap-1 text-foreground">
              <Star className="h-4 w-4 fill-amber-400 text-amber-400" />
              {property.rating.toFixed(2)}
              {property.reviewCount != null && (
                <span className="text-muted-foreground">({property.reviewCount} reviews)</span>
              )}
            </span>
          )}
          <span className="flex items-center gap-1">
            <MapPin className="h-4 w-4" />
            {property.city}, {property.country}
          </span>
        </div>
      </motion.div>

      <motion.div initial={{ opacity: 0, scale: 0.98 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.5, delay: 0.1 }}>
        <PhotoGallery photoUrls={photoUrls ?? []} />
      </motion.div>

      <div className="grid grid-cols-1 gap-10 lg:grid-cols-3">
        <div className="space-y-8 lg:col-span-2">
          <motion.div
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: 0.15 }}
            className="flex items-center justify-between border-b pb-6"
          >
            <div>
              <p className="font-medium">
                Hosted by {property.hostName ?? "the owner"}
              </p>
              <p className="text-sm text-muted-foreground">
                {[
                  property.maxGuests != null ? `${property.maxGuests} guests` : null,
                  property.bedrooms != null ? `${property.bedrooms} bedrooms` : null,
                  property.bathrooms != null ? `${property.bathrooms} baths` : null,
                ]
                  .filter(Boolean)
                  .join(" · ")}
              </p>
            </div>
            {property.hostName && (
              <img
                src={demoAvatarUrl(property.hostName)}
                alt=""
                className="h-12 w-12 rounded-full bg-muted"
              />
            )}
          </motion.div>

          {(property.bedrooms != null || property.bathrooms != null || property.maxGuests != null) && (
            <motion.div
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.2 }}
              className="flex flex-wrap gap-6 border-b pb-6"
            >
              {property.maxGuests != null && <StatItem icon={Users} label={`${property.maxGuests} guests`} />}
              {property.bedrooms != null && <StatItem icon={BedDouble} label={`${property.bedrooms} bedrooms`} />}
              {property.bathrooms != null && <StatItem icon={Bath} label={`${property.bathrooms} bathrooms`} />}
            </motion.div>
          )}

          {property.description && (
            <motion.p
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.25 }}
              className="border-b pb-6 leading-relaxed text-muted-foreground"
            >
              {property.description}
            </motion.p>
          )}

          {property.highlights && property.highlights.length > 0 && (
            <motion.div
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.3 }}
              className="border-b pb-6"
            >
              <h2 className="mb-3 font-medium">What this place offers</h2>
              <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                {property.highlights.map((h) => (
                  <div key={h} className="flex items-center gap-2 text-sm">
                    <Sparkles className="h-4 w-4 shrink-0 text-primary" />
                    {h}
                  </div>
                ))}
              </div>
            </motion.div>
          )}

          <motion.div
            initial={{ opacity: 0, y: 12 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-60px" }}
            transition={{ duration: 0.4 }}
          >
            <h2 className="mb-4 font-medium">
              {property.rating != null ? (
                <span className="flex items-center gap-1.5">
                  <Star className="h-4 w-4 fill-amber-400 text-amber-400" />
                  {property.rating.toFixed(2)} · {property.reviewCount} reviews
                </span>
              ) : (
                "Reviews"
              )}
            </h2>
            <ReviewsSection propertyId={property.id} />
          </motion.div>
        </div>

        <motion.div
          initial={{ opacity: 0, x: 16 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.45, delay: 0.2 }}
          className="lg:sticky lg:top-24 lg:h-fit"
        >
          <Card className="shadow-lg">
            <Stack gap="md">
              {property.basePrice && (
                <p className="text-xl font-semibold">
                  {formatKES(property.basePrice)}
                  <span className="text-sm font-normal text-muted-foreground"> / night</span>
                </p>
              )}
              {tripCheckIn && tripCheckOut && (
                <Button
                  onClick={() => {
                    const params = new URLSearchParams({ checkIn: tripCheckIn, checkOut: tripCheckOut });
                    router.push(`/properties/${property.id}/book?${params.toString()}`);
                  }}
                >
                  Book {tripCheckIn} → {tripCheckOut}
                </Button>
              )}
              <AvailabilityCalendar
                propertyId={property.id}
                onSelectRange={(checkIn, checkOut) => {
                  const params = new URLSearchParams({ checkIn, checkOut });
                  router.push(`/properties/${property.id}/book?${params.toString()}`);
                }}
              />
            </Stack>
          </Card>

          {property.propertyType === "RETAIL_MALL" && (
            <a href={`/properties/${property.id}/mall`} className="mt-3 block text-center text-sm underline">
              Browse store directory
            </a>
          )}
          {property.propertyType === "OFFICE" && (
            <a href={`/properties/${property.id}/office`} className="mt-3 block text-center text-sm underline">
              View meeting rooms
            </a>
          )}
        </motion.div>
      </div>
    </Stack>
  );
}

function StatItem({ icon: Icon, label }: { icon: typeof Users; label: string }) {
  return (
    <div className="flex items-center gap-2 text-sm text-muted-foreground">
      <Icon className="h-4 w-4" />
      {label}
    </div>
  );
}
