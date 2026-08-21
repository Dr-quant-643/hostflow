"use client";

import { Compass, Building2 } from "lucide-react";
import { HostflowAura } from "./hostflow-aura";
import { ProductCard } from "./product-card";

// Plain, unauthenticated landing page. Just links to each product's root —
// it does not check auth itself. Landing on /xanuos or /nazilco with no
// session is what triggers that branch's middleware redirect to its own
// login route.
export function ProductPicker() {
  return (
    <div className="relative min-h-screen overflow-hidden">
      <HostflowAura />

      {/* Giant translucent wordmark, sitting behind everything */}
      <div
        aria-hidden
        className="pointer-events-none absolute inset-x-0 top-16 flex select-none justify-center overflow-hidden sm:top-8"
      >
        <span className="whitespace-nowrap text-[22vw] font-black leading-none tracking-tighter text-purple-500/[0.07] dark:text-purple-400/[0.09]">
          HostFlow
        </span>
      </div>

      <div className="relative mx-auto flex min-h-screen max-w-6xl flex-col items-center justify-center gap-14 px-6 py-24">
        <div className="flex flex-col items-center gap-4 text-center">
          <span className="rounded-full border border-purple-400/30 bg-purple-500/10 px-4 py-1 text-xs font-semibold uppercase tracking-[0.3em] text-purple-700 dark:text-purple-300">
            Welcome to
          </span>
          <h1 className="bg-gradient-to-br from-purple-600 via-purple-500 to-sapphire-500 bg-clip-text text-5xl font-extrabold tracking-tight text-transparent sm:text-6xl">
            HostFlow
          </h1>
          <p className="max-w-md text-balance text-base italic text-muted-foreground">
            &ldquo;One address for every stay, every property, every deal.&rdquo;
          </p>
        </div>

        <div className="flex w-full flex-col gap-8 md:flex-row">
          <ProductCard
            href="/nazilco"
            icon={Compass}
            eyebrow="NazilCo"
            title="Find your next stay"
            audience="Guests & travelers"
            description="NazilCo is HostFlow's guest marketplace — discover and book vacation rentals, hotels, and serviced spaces, then manage the whole trip from one guest portal."
            bullets={[
              "Search and book vacation rentals, hotels & serviced offices",
              "Track bookings, invoices, and support tickets in one place",
              "Leave reviews and manage your profile after every stay",
            ]}
            delay={0.1}
          />
          <ProductCard
            href="/xanuos"
            icon={Building2}
            eyebrow="XanuOS"
            title="Run your properties"
            audience="Owners, managers & staff"
            description="XanuOS is HostFlow's property operations platform — manage every property, booking, and dollar from a single dashboard built for hospitality and rental businesses."
            bullets={[
              "Manage properties, bookings, rentals, malls & offices in one place",
              "Handle billing, budgets, expenses, and CRM without switching tools",
              "Coordinate maintenance, marketing, and your team from one view",
            ]}
            delay={0.25}
          />
        </div>
      </div>
    </div>
  );
}
