"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import {
  Building2,
  Wrench,
  Receipt,
  Users,
  BarChart3,
  Store,
  Briefcase,
  Megaphone,
  CalendarCheck,
  ShieldCheck,
  Zap,
  Headset,
} from "lucide-react";
import { Button } from "@hostflow/ui";

const FEATURES = [
  {
    icon: Building2,
    title: "Property portfolio",
    desc: "Track every unit, building, and listing status in one place — residential, vacation, office, or retail.",
  },
  {
    icon: Receipt,
    title: "Billing & invoicing",
    desc: "Automated rent, invoices, expenses, and budgets — no more spreadsheets chasing payments.",
  },
  {
    icon: CalendarCheck,
    title: "Leasing & rentals",
    desc: "Manage leases, tenants, and rent payments end to end, with renewals that don't fall through the cracks.",
  },
  {
    icon: Wrench,
    title: "Maintenance & work orders",
    desc: "Log assets, assign work orders, and track maintenance schedules across every property.",
  },
  {
    icon: Users,
    title: "CRM & leads",
    desc: "Keep every prospect, tenant, and owner conversation in one pipeline, not scattered across inboxes.",
  },
  {
    icon: BarChart3,
    title: "Analytics & reporting",
    desc: "Real-time occupancy, revenue, and performance reporting across your entire portfolio.",
  },
  {
    icon: Store,
    title: "Mall & retail ops",
    desc: "Purpose-built tools for retail/mall operators — tenant events, parking, and unit management.",
  },
  {
    icon: Briefcase,
    title: "Office management",
    desc: "Meeting rooms, visitor logs, and workplace bookings for managed office spaces.",
  },
  {
    icon: Megaphone,
    title: "Marketing campaigns",
    desc: "Run and track campaigns to fill vacancies faster, straight from the same dashboard.",
  },
];

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
            XanuOS is HostFlow&rsquo;s operations platform for real estate and
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

      <section className="mx-auto max-w-6xl px-6 py-16">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.5 }}
          className="mb-10 text-center"
        >
          <h2 className="text-2xl font-semibold sm:text-3xl">
            Everything your portfolio needs
          </h2>
          <p className="mt-2 text-muted-foreground">
            One platform instead of five disconnected tools.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {FEATURES.map((feature, i) => (
            <motion.div
              key={feature.title}
              initial={{ opacity: 0, y: 16 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-60px" }}
              transition={{ duration: 0.4, delay: (i % 3) * 0.08 }}
              className="rounded-2xl border border-border/60 bg-card p-6 transition-shadow hover:shadow-lg hover:shadow-purple-500/5"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gradient-to-br from-sapphire-500/15 to-purple-500/15">
                <feature.icon className="h-5 w-5 text-sapphire-600" />
              </div>
              <h3 className="mt-4 font-medium">{feature.title}</h3>
              <p className="mt-1.5 text-sm text-muted-foreground">{feature.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>

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
              <Link href="/">Explore other HostFlow products</Link>
            </Button>
          </div>
        </motion.div>
      </section>
    </div>
  );
}
