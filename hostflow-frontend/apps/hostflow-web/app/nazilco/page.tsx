"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Button } from "@hostflow/ui";
import { useDiscoverProperties } from "@hostflow/api-client/src/hooks/use-public-properties";
import { PropertyCard } from "@/components/nazilco/discover/property-card";
import { HeroSearchBar } from "@/components/nazilco/home/hero-search-bar";

const HERO_IMAGE =
  "https://images.unsplash.com/photo-1523805009345-7448845a9e53?auto=format&fit=crop&w=2000&q=80";

export default function HomePage() {
  const { data: properties } = useDiscoverProperties(6);

  return (
    <div>
      <section className="relative flex min-h-[85vh] items-center justify-center overflow-hidden">
        <motion.div
          initial={{ scale: 1.08, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 1.4, ease: "easeOut" }}
          className="absolute inset-0"
        >
          <img
            src={HERO_IMAGE}
            alt=""
            className="h-full w-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/30 to-black/10" />
        </motion.div>

        <div className="relative z-10 mx-auto flex w-full max-w-3xl flex-col items-center gap-6 px-6 text-center text-white">
          <motion.span
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.6 }}
            className="rounded-full border border-white/30 bg-white/10 px-4 py-1 text-xs font-medium uppercase tracking-widest backdrop-blur-sm"
          >
            Stays worth the trip
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
            Hand-picked homes, villas, and cabins around the world — booked in
            minutes, remembered for years.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.65, duration: 0.7 }}
            className="mt-2 w-full"
          >
            <HeroSearchBar />
          </motion.div>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-6 py-16">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.5 }}
          className="mb-8 flex items-end justify-between"
        >
          <div>
            <h2 className="text-2xl font-semibold sm:text-3xl">Featured stays</h2>
            <p className="mt-1 text-muted-foreground">
              A few of our guests&rsquo; favorites this season
            </p>
          </div>
          <Button variant="outline" asChild>
            <Link href="/discover">Browse all</Link>
          </Button>
        </motion.div>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {(properties ?? []).map((property, i) => (
            <motion.div
              key={property.id}
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-60px" }}
              transition={{ duration: 0.45, delay: i * 0.06 }}
            >
              <PropertyCard property={property} />
            </motion.div>
          ))}
        </div>
      </section>

      <section className="border-t bg-muted/40">
        <div className="mx-auto grid max-w-6xl grid-cols-1 gap-8 px-6 py-16 sm:grid-cols-3">
          {[
            { title: "Verified hosts", desc: "Every listing is reviewed before it goes live." },
            { title: "Flexible dates", desc: "Check availability instantly, no back-and-forth." },
            { title: "24/7 support", desc: "Real humans, one message away, day or night." },
          ].map((item, i) => (
            <motion.div
              key={item.title}
              initial={{ opacity: 0, y: 16 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-60px" }}
              transition={{ duration: 0.45, delay: i * 0.08 }}
              className="text-center sm:text-left"
            >
              <h3 className="text-lg font-medium">{item.title}</h3>
              <p className="mt-1 text-sm text-muted-foreground">{item.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>
    </div>
  );
}
