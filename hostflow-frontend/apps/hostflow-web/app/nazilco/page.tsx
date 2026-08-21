"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Button } from "@hostflow/ui";
import { useDiscoverProperties } from "@hostflow/api-client/src/hooks/use-public-properties";
import { PropertyCard } from "@/components/nazilco/discover/property-card";
import { HeroSearchBar } from "@/components/nazilco/home/hero-search-bar";

const HERO_IMAGE =
  "https://images.unsplash.com/photo-1523805009345-7448845a9e53?auto=format&fit=crop&w=2000&q=80";

const CATEGORY_PILLS = [
  { emoji: "🏡", label: "Vacation rentals", href: "/nazilco/discover?type=VACATION_RENTAL" },
  { emoji: "🏢", label: "Apartments", href: "/nazilco/discover?type=RESIDENTIAL" },
  { emoji: "🏨", label: "Hotels", href: "/nazilco/discover?type=HOTEL" },
  { emoji: "📍", label: "Near me", href: "/nazilco/discover?nearMe=1" },
  { emoji: "✨", label: "All stays", href: "/nazilco/discover" },
];

export default function HomePage() {
  const { data: properties } = useDiscoverProperties(9);

  return (
    <div>
      <section className="relative flex min-h-[85vh] items-center justify-center overflow-hidden">
        <motion.div
          initial={{ scale: 1.08, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 1.4, ease: "easeOut" }}
          className="absolute inset-0"
        >
          <img src={HERO_IMAGE} alt="" className="h-full w-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-[#180f2b]/90 via-[#2c1b4d]/45 to-black/10" />
          <div className="absolute inset-0 bg-gradient-to-br from-sapphire-500/10 via-transparent to-purple-500/20" />
        </motion.div>

        <div className="relative z-10 mx-auto flex w-full max-w-3xl flex-col items-center gap-6 px-6 text-center text-white">
          <motion.span
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.6 }}
            className="rounded-full border border-white/30 bg-white/10 px-4 py-1 text-xs font-medium uppercase tracking-widest backdrop-blur-sm"
          >
            📌 Pin your next stay
          </motion.span>
          <motion.h1
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.35, duration: 0.7 }}
            className="text-4xl font-semibold leading-tight sm:text-6xl"
          >
            Find your next stay,
            <br className="hidden sm:block" /> beautifully.
          </motion.h1>
          <motion.p
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5, duration: 0.7 }}
            className="max-w-xl text-base text-white/85 sm:text-lg"
          >
            Browse homes, villas, and hotels the way you browse ideas —
            hand-picked, visual, and booked in minutes.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.65, duration: 0.7 }}
            className="mt-2 w-full"
          >
            <HeroSearchBar />
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.8, duration: 0.7 }}
            className="flex flex-wrap items-center justify-center gap-2 pt-1"
          >
            {CATEGORY_PILLS.map((pill) => (
              <Link
                key={pill.label}
                href={pill.href}
                className="flex items-center gap-1.5 rounded-full border border-white/25 bg-white/10 px-3.5 py-1.5 text-sm text-white/90 backdrop-blur-sm transition-colors hover:border-white/40 hover:bg-white/20"
              >
                <span>{pill.emoji}</span>
                {pill.label}
              </Link>
            ))}
          </motion.div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-16">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.5 }}
          className="mb-8 flex items-end justify-between"
        >
          <div>
            <h2 className="text-2xl font-semibold sm:text-3xl">
              <span className="bg-gradient-to-r from-sapphire-600 to-purple-600 bg-clip-text text-transparent">
                Pinned
              </span>{" "}
              for you
            </h2>
            <p className="mt-1 text-muted-foreground">
              A few of our guests&rsquo; favorites this season
            </p>
          </div>
          <Button variant="outline" asChild>
            <Link href="/nazilco/discover">Browse all</Link>
          </Button>
        </motion.div>

        <div className="columns-1 gap-5 sm:columns-2 lg:columns-3">
          {(properties ?? []).map((property, i) => (
            <motion.div
              key={property.id}
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-60px" }}
              transition={{ duration: 0.45, delay: i * 0.06 }}
              className="mb-5 break-inside-avoid"
            >
              <PropertyCard property={property} />
            </motion.div>
          ))}
        </div>
      </section>

      <section className="border-y border-border/60 bg-gradient-to-b from-sapphire-50/60 to-purple-50/40 dark:from-sapphire-950/10 dark:to-purple-950/10">
        <div className="mx-auto grid max-w-6xl grid-cols-1 gap-8 px-6 py-16 sm:grid-cols-3">
          {[
            { emoji: "🛡️", title: "Verified hosts", desc: "Every listing is reviewed before it goes live." },
            { emoji: "⚡", title: "Flexible dates", desc: "Check availability instantly, no back-and-forth." },
            { emoji: "💬", title: "24/7 support", desc: "Real humans, one message away, day or night." },
          ].map((item, i) => (
            <motion.div
              key={item.title}
              initial={{ opacity: 0, y: 16 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-60px" }}
              transition={{ duration: 0.45, delay: i * 0.08 }}
              className="rounded-2xl border border-border/50 bg-background/60 p-6 text-center backdrop-blur-sm sm:text-left"
            >
              <span className="text-2xl">{item.emoji}</span>
              <h3 className="mt-2 text-lg font-medium">{item.title}</h3>
              <p className="mt-1 text-sm text-muted-foreground">{item.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-6 py-16">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-60px" }}
          transition={{ duration: 0.5 }}
          className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-sapphire-600 via-purple-600 to-purple-700 px-8 py-12 text-white sm:px-14 sm:py-16"
        >
          <div className="pointer-events-none absolute -right-16 -top-16 h-64 w-64 rounded-full bg-white/10 blur-3xl" />
          <div className="pointer-events-none absolute -bottom-20 left-10 h-56 w-56 rounded-full bg-white/10 blur-3xl" />
          <div className="relative z-10 flex flex-col items-start gap-4 sm:max-w-xl">
            <span className="rounded-full border border-white/30 bg-white/10 px-3 py-1 text-xs font-medium uppercase tracking-widest">
              🏘️ For property owners &amp; managers
            </span>
            <h2 className="text-2xl font-semibold sm:text-3xl">
              Get discovered by travelers actively looking to book.
            </h2>
            <p className="text-white/85">
              NazilCo puts your listing in front of guests browsing by photo,
              not just filters — the properties that stand out visually are
              the ones that get booked. List with us and reach a growing
              audience of ready-to-book travelers.
            </p>
            <Button size="lg" className="mt-2 bg-white text-purple-700 hover:bg-white/90" asChild>
              <Link href="/nazilco/signup">List your property</Link>
            </Button>
          </div>
        </motion.div>
      </section>
    </div>
  );
}
