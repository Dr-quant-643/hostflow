"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { ShieldCheck, Zap, Headset } from "lucide-react";
import { Button } from "@hostflow/ui";
import { FeatureShowcase } from "@/components/xanuos/marketing/feature-showcase";
import {
  PortfolioMockup,
  BillingMockup,
  LeasingMockup,
  MaintenanceMockup,
  CrmMockup,
  AnalyticsMockup,
  MallMockup,
  OfficeMockup,
  MarketingMockup,
} from "@/components/xanuos/marketing/feature-mockups";

const TRUST_BADGES = [
  { icon: ShieldCheck, label: "Role-based access for your whole team" },
  { icon: Zap, label: "Real-time data, no manual syncing" },
  { icon: Headset, label: "Support when you need it" },
];

export default function XanuosLandingPage() {
  return (
    <div className="min-h-screen bg-background">
      <header className="flex items-center justify-between border-b border-border/60 px-6 py-4">
        <div className="flex items-center gap-2 text-lg font-semibold tracking-tight">
          <span className="text-xl">🏢</span>
          <span className="bg-gradient-to-br from-sapphire-500 via-purple-500 to-purple-600 bg-clip-text text-transparent">
            XanuOS
          </span>
        </div>
        <div className="flex items-center gap-3">
          <a
            href="/xanuos/api/auth/login"
            className="text-sm font-medium text-foreground/80 hover:text-foreground"
          >
            Log in
          </a>
          <Button
            size="sm"
            className="bg-gradient-to-r from-sapphire-500 to-purple-600 hover:opacity-90"
            asChild
          >
            <Link href="/xanuos/signup">Get started</Link>
          </Button>
        </div>
      </header>

      <section className="relative overflow-hidden">
        <div
          aria-hidden
          className="pointer-events-none absolute inset-0 -z-10"
          style={{
            background:
              "radial-gradient(60% 50% at 20% 0%, rgba(124,58,237,0.14), transparent), radial-gradient(50% 40% at 90% 10%, rgba(37,99,235,0.12), transparent)",
          }}
        />
        <div className="mx-auto max-w-4xl px-6 py-20 text-center">
          <motion.span
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="inline-flex items-center gap-1.5 rounded-full border border-border bg-muted/50 px-4 py-1 text-xs font-medium uppercase tracking-widest text-muted-foreground"
          >
            🏢 Built for property owners & managers
          </motion.span>
          <motion.h1
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.6 }}
            className="mt-6 text-4xl font-semibold leading-tight sm:text-6xl"
          >
            Run every property, lease, and{" "}
            <span className="bg-gradient-to-r from-sapphire-600 to-purple-600 bg-clip-text text-transparent">
              team
            </span>{" "}
            — from one dashboard.
          </motion.h1>
          <motion.p
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.6 }}
            className="mx-auto mt-5 max-w-2xl text-base text-muted-foreground sm:text-lg"
          >
            XanuOS is RvanaFlow&rsquo;s operations platform for real estate and
            property managers — billing, leasing, maintenance, and CRM, all
            in one place, so you spend less time on admin and more time
            growing your portfolio.
          </motion.p>
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3, duration: 0.6 }}
            className="mt-8 flex flex-wrap items-center justify-center gap-3"
          >
            <Button
              size="lg"
              className="bg-gradient-to-r from-sapphire-500 to-purple-600 hover:opacity-90"
              asChild
            >
              <Link href="/xanuos/signup">Get started free</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <a href="/xanuos/api/auth/login">Log in to your workspace</a>
            </Button>
          </motion.div>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.5, duration: 0.6 }}
            className="mt-10 flex flex-wrap items-center justify-center gap-x-8 gap-y-3"
          >
            {TRUST_BADGES.map((badge) => (
              <span
                key={badge.label}
                className="flex items-center gap-1.5 text-sm text-muted-foreground"
              >
                <badge.icon className="h-4 w-4 text-sapphire-600" />
                {badge.label}
              </span>
            ))}
          </motion.div>
        </div>
      </section>

      <div className="mx-auto max-w-3xl px-6 pt-16 text-center">
        <h2 className="text-2xl font-semibold sm:text-3xl">
          Everything your portfolio needs — see it in action
        </h2>
        <p className="mt-2 text-muted-foreground">
          One platform instead of five disconnected tools. Here&rsquo;s what each one actually looks like.
        </p>
      </div>

      <FeatureShowcase
        emoji="🏢"
        badge="Property portfolio"
        title="Every property, one live view"
        quote="I used to have three spreadsheets open just to know my occupancy. Now it's one screen."
        description="Track units, occupancy, and rent across every building you manage — residential, vacation, office, or retail — updated the moment a lease changes."
        photo="https://images.unsplash.com/photo-1460317442991-0ec209397118?auto=format&fit=crop&w=400&q=60"
        photoAlt="Apartment building exterior"
        mockupPath="app.hostflow.io/xanuos/properties"
        mockup={<PortfolioMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="See your properties in one view"
      />

      <FeatureShowcase
        reverse
        emoji="🧾"
        badge="Billing & invoicing"
        title="Rent that chases itself"
        quote="No more month-end scramble to figure out who's paid and who hasn't."
        description="Auto-generate rent invoices, track payments, and see overdue tenants flagged instantly — instead of digging through bank statements."
        photo="https://images.unsplash.com/photo-1554224155-6726b3ff858f?auto=format&fit=crop&w=400&q=60"
        photoAlt="Invoices and calculator on a desk"
        mockupPath="app.hostflow.io/xanuos/billing"
        mockup={<BillingMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Automate your rent collection"
      />

      <FeatureShowcase
        emoji="📋"
        badge="Leasing & rentals"
        title="Never miss a renewal again"
        quote="We got a 60-day heads up on a lease ending — that's a month's rent we didn't lose."
        description="Every lease, every tenant, every renewal date in one place — with alerts before a vacancy costs you money."
        photo="https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=400&q=60"
        photoAlt="House keys on a table"
        mockupPath="app.hostflow.io/xanuos/rental"
        mockup={<LeasingMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Track your leases automatically"
      />

      <FeatureShowcase
        reverse
        emoji="🔧"
        badge="Maintenance & work orders"
        title="A leaking tap becomes a ticket, not a lost tenant"
        quote="Tenants report it, a technician gets assigned, and I can see it's actually done."
        description="Every maintenance request tracked from reported to resolved, with a technician assigned and a paper trail — no more 'I thought someone else was handling it.'"
        photo="https://images.unsplash.com/photo-1621905251189-08b45d6a269e?auto=format&fit=crop&w=400&q=60"
        photoAlt="Maintenance technician at work"
        mockupPath="app.hostflow.io/xanuos/maintenance"
        mockup={<MaintenanceMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Never lose a maintenance request"
      />

      <FeatureShowcase
        emoji="🤝"
        badge="CRM & leads"
        title="Every prospect, one pipeline"
        quote="I stopped losing leads in a WhatsApp thread I can never find again."
        description="Track a prospect from first inquiry to signed lease — who viewed what, who's ready to sign, who's gone quiet."
        photo="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=400&q=60"
        photoAlt="Team collaborating around laptops"
        mockupPath="app.hostflow.io/xanuos/crm"
        mockup={<CrmMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Organize your leads"
      />

      <FeatureShowcase
        reverse
        emoji="📊"
        badge="Analytics & reporting"
        title="Know your numbers before your accountant does"
        quote="Occupancy, revenue, and costs — I check one dashboard now, not five spreadsheets."
        description="Real-time occupancy trends, revenue by property, and maintenance costs, so decisions are made on data, not guesswork."
        photo="https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=400&q=60"
        photoAlt="Analytics dashboard on a monitor"
        mockupPath="app.hostflow.io/xanuos/analytics"
        mockup={<AnalyticsMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="See your portfolio's numbers"
      />

      <FeatureShowcase
        emoji="🛍️"
        badge="Mall & retail ops"
        title="From anchor tenant to pop-up stall, one directory"
        quote="Store leases, parking, and mall events — finally in one place instead of three different systems."
        description="Purpose-built for retail and mall operators: manage tenant leases, monitor parking capacity, and run mall-wide events from a single screen."
        photo="https://images.unsplash.com/photo-1567958451986-2de427a4a0be?auto=format&fit=crop&w=400&q=60"
        photoAlt="Retail store interior"
        mockupPath="app.hostflow.io/xanuos/mall"
        mockup={<MallMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Manage your retail space"
      />

      <FeatureShowcase
        reverse
        emoji="🏙️"
        badge="Office management"
        title="No more double-booked boardrooms"
        quote="Visitors sign in at the door, and I actually know who's in the building."
        description="Meeting rooms booked in seconds, visitor check-ins logged automatically — no more a logbook nobody can find."
        photo="https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=400&q=60"
        photoAlt="Modern open-plan office"
        mockupPath="app.hostflow.io/xanuos/office"
        mockup={<OfficeMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Streamline your office"
      />

      <FeatureShowcase
        emoji="📣"
        badge="Marketing campaigns"
        title="Fill vacancies faster, with numbers to prove it"
        quote="I can finally see which listing promo actually turns into a signed lease."
        description="Launch a campaign for a vacant unit, track views turning into inquiries, and know exactly what's working — not just what you hope is working."
        photo="https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=400&q=60"
        photoAlt="Marketing analytics on a laptop"
        mockupPath="app.hostflow.io/xanuos/marketing"
        mockup={<MarketingMockup />}
        ctaHref="/xanuos/signup"
        ctaLabel="Launch your first campaign"
      />

      <section className="border-t border-border/60 bg-gradient-to-b from-sapphire-50/60 to-purple-50/40 dark:from-sapphire-950/10 dark:to-purple-950/10">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-60px" }}
          transition={{ duration: 0.5 }}
          className="mx-auto max-w-3xl px-6 py-16 text-center"
        >
          <h2 className="text-2xl font-semibold sm:text-3xl">
            See it with your own properties.
          </h2>
          <p className="mt-3 text-muted-foreground">
            Create your workspace and start adding properties in minutes —
            no sales call required.
          </p>
          <div className="mt-6 flex flex-wrap items-center justify-center gap-3">
            <Button
              size="lg"
              className="bg-gradient-to-r from-sapphire-500 to-purple-600 hover:opacity-90"
              asChild
            >
              <Link href="/xanuos/signup">Create your workspace</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link href="/">Explore other RvanaFlow products</Link>
            </Button>
          </div>
        </motion.div>
      </section>
    </div>
  );
}
