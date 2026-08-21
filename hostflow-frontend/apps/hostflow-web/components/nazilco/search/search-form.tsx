"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter, useSearchParams } from "next/navigation";
import { motion } from "framer-motion";
import { MapPin, CalendarDays, Users, Minus, Plus, Search } from "lucide-react";
import {
  propertySearchFormSchema,
  type PropertySearchFormValues,
} from "@hostflow/validation";

const QUICK_DESTINATIONS = ["Nairobi", "Diani Beach", "Narok", "Kilimani"];

export function SearchForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [destinationFocused, setDestinationFocused] = useState(false);

  const form = useForm<PropertySearchFormValues>({
    resolver: zodResolver(propertySearchFormSchema),
    defaultValues: {
      destination: searchParams.get("destination") ?? "",
      checkIn: searchParams.get("checkIn") ?? "",
      checkOut: searchParams.get("checkOut") ?? "",
      guests: Number(searchParams.get("guests")) || 1,
    },
  });

  const guests = form.watch("guests");
  const destination = form.watch("destination");

  const onSubmit = form.handleSubmit((values) => {
    const params = new URLSearchParams({
      checkIn: values.checkIn,
      checkOut: values.checkOut,
      guests: String(values.guests),
      ...(values.destination ? { destination: values.destination } : {}),
    });
    router.push(`/nazilco/search?${params.toString()}`);
  });

  return (
    <form onSubmit={onSubmit}>
      <motion.div
        initial={{ opacity: 0, y: -8 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
        className="relative rounded-2xl border border-border/60 bg-background/70 p-4 shadow-sm backdrop-blur-md"
      >
        <div className="flex flex-col gap-3 sm:flex-row sm:items-stretch">
          <div className="relative flex-[1.4]">
            <label className="mb-1 block text-xs font-medium text-muted-foreground">Destination</label>
            <div className="flex items-center gap-2 rounded-full border border-border bg-background px-3 py-2.5">
              <MapPin className="h-4 w-4 shrink-0 text-muted-foreground" />
              <input
                {...form.register("destination")}
                onFocus={() => setDestinationFocused(true)}
                onBlur={() => setTimeout(() => setDestinationFocused(false), 120)}
                placeholder="Where to? (e.g. Nairobi)"
                className="w-full bg-transparent text-sm outline-none placeholder:text-muted-foreground/70"
                autoComplete="off"
              />
            </div>
            {form.formState.errors.destination && (
              <p className="mt-1 text-xs text-destructive">{form.formState.errors.destination.message}</p>
            )}
            {destinationFocused && (
              <motion.div
                initial={{ opacity: 0, y: -4 }}
                animate={{ opacity: 1, y: 0 }}
                className="absolute left-0 top-full z-20 mt-1.5 flex w-full flex-wrap gap-1.5 rounded-xl border border-border/60 bg-background p-2 shadow-lg"
              >
                {QUICK_DESTINATIONS.filter((d) => d.toLowerCase() !== (destination ?? "").toLowerCase()).map(
                  (d) => (
                    <button
                      key={d}
                      type="button"
                      onMouseDown={() => form.setValue("destination", d)}
                      className="rounded-full border border-border px-2.5 py-1 text-xs text-muted-foreground hover:border-sapphire-300 hover:bg-sapphire-50 hover:text-sapphire-700"
                    >
                      {d}
                    </button>
                  ),
                )}
              </motion.div>
            )}
          </div>

          <div className="flex-1">
            <label className="mb-1 flex items-center gap-1 text-xs font-medium text-muted-foreground">
              <CalendarDays className="h-3 w-3" /> Check In
            </label>
            <input
              type="date"
              {...form.register("checkIn")}
              className="w-full rounded-full border border-border bg-background px-3 py-2.5 text-sm outline-none focus:border-primary"
            />
            {form.formState.errors.checkIn && (
              <p className="mt-1 text-xs text-destructive">{form.formState.errors.checkIn.message}</p>
            )}
          </div>

          <div className="flex-1">
            <label className="mb-1 flex items-center gap-1 text-xs font-medium text-muted-foreground">
              <CalendarDays className="h-3 w-3" /> Check Out
            </label>
            <input
              type="date"
              {...form.register("checkOut")}
              className="w-full rounded-full border border-border bg-background px-3 py-2.5 text-sm outline-none focus:border-primary"
            />
            {form.formState.errors.checkOut && (
              <p className="mt-1 text-xs text-destructive">{form.formState.errors.checkOut.message}</p>
            )}
          </div>

          <div className="flex-1">
            <label className="mb-1 flex items-center gap-1 text-xs font-medium text-muted-foreground">
              <Users className="h-3 w-3" /> Guests
            </label>
            <div className="flex items-center justify-between gap-2 rounded-full border border-border bg-background px-2 py-1.5">
              <button
                type="button"
                aria-label="Fewer guests"
                onClick={() => form.setValue("guests", Math.max(1, guests - 1))}
                className="rounded-full p-1.5 text-muted-foreground transition-colors hover:bg-muted disabled:opacity-40"
                disabled={guests <= 1}
              >
                <Minus className="h-3.5 w-3.5" />
              </button>
              <span className="text-sm font-medium">{guests}</span>
              <button
                type="button"
                aria-label="More guests"
                onClick={() => form.setValue("guests", Math.min(16, guests + 1))}
                className="rounded-full p-1.5 text-muted-foreground transition-colors hover:bg-muted disabled:opacity-40"
                disabled={guests >= 16}
              >
                <Plus className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>

          <div className="flex items-end">
            <motion.button
              type="submit"
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.97 }}
              className="flex items-center justify-center gap-2 rounded-full bg-sapphire-600 px-5 py-2.5 text-sm font-medium text-white shadow-sm transition-colors hover:bg-sapphire-700 sm:h-[42px]"
            >
              <Search className="h-4 w-4" />
              Search
            </motion.button>
          </div>
        </div>
      </motion.div>
    </form>
  );
}
