"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Search, MapPin, Calendar, Users, LocateFixed } from "lucide-react";

export function HeroSearchBar() {
  const router = useRouter();
  const [destination, setDestination] = useState("");
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [guests, setGuests] = useState(2);

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const params = new URLSearchParams({
      checkIn: checkIn || defaultDate(14),
      checkOut: checkOut || defaultDate(17),
      guests: String(guests),
      ...(destination ? { destination } : {}),
    });
    router.push(`/search?${params.toString()}`);
  };

  return (
    <div className="mx-auto w-full max-w-3xl space-y-3">
    <form
      onSubmit={onSubmit}
      className="flex w-full flex-col gap-2 rounded-2xl bg-white/95 p-2 text-foreground shadow-2xl backdrop-blur-sm sm:flex-row sm:items-stretch sm:rounded-full"
    >
      <label className="flex flex-1 items-center gap-2 rounded-full px-4 py-2.5 hover:bg-black/[0.03]">
        <MapPin className="h-4 w-4 shrink-0 text-muted-foreground" />
        <div className="flex flex-col text-left">
          <span className="text-[11px] font-medium text-muted-foreground">
            Destination
          </span>
          <input
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            placeholder="Anywhere"
            className="w-full bg-transparent text-sm outline-none placeholder:text-muted-foreground/70"
          />
        </div>
      </label>

      <div className="hidden w-px self-stretch bg-border sm:block" />

      <label className="flex flex-1 items-center gap-2 rounded-full px-4 py-2.5 hover:bg-black/[0.03]">
        <Calendar className="h-4 w-4 shrink-0 text-muted-foreground" />
        <div className="flex flex-col text-left">
          <span className="text-[11px] font-medium text-muted-foreground">
            Check in — Check out
          </span>
          <div className="flex gap-1.5">
            <input
              type="date"
              value={checkIn}
              onChange={(e) => setCheckIn(e.target.value)}
              className="w-full bg-transparent text-sm outline-none"
            />
            <span className="text-muted-foreground">–</span>
            <input
              type="date"
              value={checkOut}
              onChange={(e) => setCheckOut(e.target.value)}
              className="w-full bg-transparent text-sm outline-none"
            />
          </div>
        </div>
      </label>

      <div className="hidden w-px self-stretch bg-border sm:block" />

      <label className="flex items-center gap-2 rounded-full px-4 py-2.5 hover:bg-black/[0.03]">
        <Users className="h-4 w-4 shrink-0 text-muted-foreground" />
        <div className="flex flex-col text-left">
          <span className="text-[11px] font-medium text-muted-foreground">Guests</span>
          <input
            type="number"
            min={1}
            max={16}
            value={guests}
            onChange={(e) => setGuests(Number(e.target.value) || 1)}
            className="w-14 bg-transparent text-sm outline-none"
          />
        </div>
      </label>

      <button
        type="submit"
        className="flex items-center justify-center gap-2 rounded-full bg-primary px-6 py-3 text-sm font-medium text-primary-foreground transition-transform hover:scale-[1.03] active:scale-[0.98] sm:px-5"
      >
        <Search className="h-4 w-4" />
        <span className="sm:hidden">Search</span>
      </button>
    </form>

      <Link
        href="/discover?nearMe=1"
        className="mx-auto flex w-fit items-center gap-1.5 rounded-full bg-white/10 px-4 py-1.5 text-xs font-medium text-white/90 backdrop-blur-sm transition-colors hover:bg-white/20"
      >
        <LocateFixed className="h-3.5 w-3.5" />
        Show stays near me
      </Link>
    </div>
  );
}

function defaultDate(daysFromNow: number): string {
  const d = new Date();
  d.setDate(d.getDate() + daysFromNow);
  return d.toISOString().slice(0, 10);
}
