"use client";

import { Compass, Building2 } from "lucide-react";
import { HostflowAura } from "./hostflow-aura";
import { FloatingIcons } from "./floating-icons";
import { ProductCard } from "./product-card";

const MARKETING_PILLS = [
  "⚡ Real-time bookings",
  "🔐 Tenant-isolated accounts",
  "🌍 Two worlds, one platform",
];

// Plain, unauthenticated landing page. Just links to each product's root —
// it does not check auth itself. Landing on /xanuos or /nazilco with no
// session is what triggers that branch's middleware redirect to its own
// login route.
export function ProductPicker() {
  return (
    <div className="relative min-h-screen overflow-hidden">
      <HostflowAura />
      <FloatingIcons />

      {/* Small, soft, translucent wordmark — a watermark, not a headline */}
      <div
        aria-hidden
        className="pointer-events-none absolute inset-x-0 top-10 flex select-none justify-center overflow-hidden"
      >
        <span className="whitespace-nowrap text-[9vw] font-light leading-none tracking-normal text-purple-200/[0.06]">
          RvanaFlow
        </span>
      </div>

      <div className="relative mx-auto flex min-h-screen max-w-6xl flex-col items-center justify-center gap-12 px-6 py-24">
        <div className="flex flex-col items-center gap-4 text-center">
          <span className="rounded-full border border-purple-300/25 bg-purple-500/15 px-4 py-1 text-xs font-semibold uppercase tracking-[0.3em] text-purple-200">
            Welcome to
          </span>
          <h1 className="bg-gradient-to-br from-purple-300 via-purple-200 to-sapphire-300 bg-clip-text text-5xl font-extrabold tracking-tight text-transparent sm:text-6xl">
            RvanaFlow
          </h1>
          <p className="max-w-md text-balance text-base italic text-white/60">
            &ldquo;One address for every stay, every property, every deal.&rdquo;
          </p>

          <div className="mt-2 flex flex-wrap items-center justify-center gap-3">
            {MARKETING_PILLS.map((pill) => (
              <span
                key={pill}
                className="rounded-full border border-white/10 bg-white/[0.05] px-4 py-1.5 text-sm text-white/70 backdrop-blur-sm"
              >
                {pill}
              </span>
            ))}
          </div>
        </div>

        <div className="flex w-full flex-col gap-10 md:flex-row">
          <ProductCard
            href="/nazilco"
            icon={Compass}
            emoji="🧭"
            eyebrow="NazilCo"
            title="Find your next stay"
            audience="Guests & travelers"
            description="NazilCo is RvanaFlow's guest marketplace — discover and book vacation rentals, hotels, and serviced spaces, then manage the whole trip from one guest portal."
            bullets={[
              { emoji: "🏨", text: "Search and book vacation rentals, hotels & serviced offices" },
              { emoji: "🧾", text: "Track bookings, invoices, and support tickets in one place" },
              { emoji: "⭐", text: "Leave reviews and manage your profile after every stay" },
            ]}
            delay={0.1}
          />
          <ProductCard
            href="/xanuos"
            icon={Building2}
            emoji="🏙️"
            eyebrow="XanuOS"
            title="Run your properties"
            audience="Owners, managers & staff"
            description="XanuOS is RvanaFlow's property operations platform — manage every property, booking, and dollar from a single dashboard built for hospitality and rental businesses."
            bullets={[
              { emoji: "🏘️", text: "Manage properties, bookings, rentals, malls & offices in one place" },
              { emoji: "💰", text: "Handle billing, budgets, expenses, and CRM without switching tools" },
              { emoji: "🛠️", text: "Coordinate maintenance, marketing, and your team from one view" },
            ]}
            delay={0.25}
          />
        </div>
      </div>
    </div>
  );
}
