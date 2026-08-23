"use client";

import { motion } from "framer-motion";
import { Star } from "lucide-react";
import { Skeleton } from "@hostflow/ui";
import { usePropertyReviews } from "@hostflow/api-client/src/hooks/use-public-properties";
import { demoAvatarUrl } from "@/lib/demo-data";

export function ReviewsSection({ propertyId }: { propertyId: string }) {
  const { data, isLoading } = usePropertyReviews(propertyId);

  if (isLoading) return <Skeleton className="h-40 w-full" />;
  if (!data || data.length === 0) return null;

  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
      {data.map((review, i) => (
        <motion.div
          key={review.id}
          initial={{ opacity: 0, y: 12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-40px" }}
          transition={{ duration: 0.4, delay: i * 0.06 }}
          className="rounded-xl border border-border/60 p-4"
        >
          <div className="flex items-center gap-3">
            <img
              src={demoAvatarUrl(review.guestName)}
              alt=""
              className="h-10 w-10 rounded-full bg-muted"
            />
            <div>
              <p className="text-sm font-medium">{review.guestName}</p>
              <p className="text-xs text-muted-foreground">
                {new Date(review.createdAt).toLocaleDateString(undefined, { month: "long", year: "numeric" })}
              </p>
            </div>
          </div>
          <div className="mt-2 flex gap-0.5">
            {Array.from({ length: 5 }).map((_, s) => (
              <Star
                key={s}
                className="h-3.5 w-3.5"
                fill={s < review.rating ? "#f59e0b" : "transparent"}
                color={s < review.rating ? "#f59e0b" : "#d1d5db"}
              />
            ))}
          </div>
          <p className="mt-2 text-sm text-muted-foreground">{review.comment}</p>
        </motion.div>
      ))}
    </div>
  );
}
