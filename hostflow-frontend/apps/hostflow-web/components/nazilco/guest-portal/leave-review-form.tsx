"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { motion } from "framer-motion";
import { Star, CheckCircle2 } from "lucide-react";
import {
  guestReviewFormSchema,
  type GuestReviewFormValues,
} from "@hostflow/validation";
import { Button, Textarea, Stack, toast } from "@hostflow/ui";
import { useDemoSubmitReview } from "@/lib/demo-hooks";

export function LeaveReviewForm({ bookingId }: { bookingId: string }) {
  const [submitted, setSubmitted] = useState(false);
  const [hovered, setHovered] = useState(0);
  const submitReview = useDemoSubmitReview();
  const form = useForm<GuestReviewFormValues>({
    resolver: zodResolver(guestReviewFormSchema),
    defaultValues: { rating: 5, comment: "" },
  });

  if (submitted) {
    return (
      <motion.p
        initial={{ opacity: 0, y: 6 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center gap-1.5 text-sm text-muted-foreground"
      >
        <CheckCircle2 className="h-4 w-4 text-success" />
        Thanks for your feedback!
      </motion.p>
    );
  }

  const rating = form.watch("rating");

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await submitReview.mutateAsync({ bookingId, ...values });
      setSubmitted(true);
      toast.success("Review submitted");
    } catch {
      toast.error("Couldn't submit your review");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Stack direction="row" gap="sm" align="center">
          {[1, 2, 3, 4, 5].map((n) => (
            <motion.button
              key={n}
              type="button"
              whileHover={{ scale: 1.15 }}
              whileTap={{ scale: 0.9 }}
              onMouseEnter={() => setHovered(n)}
              onMouseLeave={() => setHovered(0)}
              onClick={() => form.setValue("rating", n)}
              aria-label={`Rate ${n} out of 5`}
            >
              <Star
                className="h-7 w-7 transition-colors"
                fill={n <= (hovered || rating) ? "#f59e0b" : "transparent"}
                color={n <= (hovered || rating) ? "#f59e0b" : "#d1d5db"}
              />
            </motion.button>
          ))}
        </Stack>
        <Textarea
          label="Comment (optional)"
          {...form.register("comment")}
          error={form.formState.errors.comment?.message}
        />
        <Button type="submit" loading={submitReview.isPending}>
          Submit Review
        </Button>
      </Stack>
    </form>
  );
}
