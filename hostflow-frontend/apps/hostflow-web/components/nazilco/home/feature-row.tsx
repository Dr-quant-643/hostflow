"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { ArrowRight } from "lucide-react";

export function FeatureRow({
  reverse,
  emoji,
  badge,
  quote,
  image,
  alt,
  ctaHref,
  ctaLabel,
}: {
  reverse?: boolean;
  emoji: string;
  badge: string;
  quote: string;
  image: string;
  alt: string;
  ctaHref: string;
  ctaLabel: string;
}) {
  return (
    <section className="mx-auto max-w-6xl px-6 py-14">
      <div className={`flex flex-col items-center gap-10 lg:flex-row ${reverse ? "lg:flex-row-reverse" : ""}`}>
        <motion.div
          initial={{ opacity: 0, x: reverse ? 40 : -40 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.6 }}
          className="w-full lg:w-1/2"
        >
          <div className="group relative overflow-hidden rounded-3xl shadow-xl shadow-purple-500/10">
            <motion.img
              src={image}
              alt={alt}
              whileHover={{ scale: 1.04 }}
              transition={{ duration: 0.6, ease: "easeOut" }}
              className="h-72 w-full object-cover sm:h-96"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/30 via-transparent to-transparent" />
            <span className="absolute left-4 top-4 flex h-11 w-11 items-center justify-center rounded-full bg-white/90 text-xl shadow-md backdrop-blur-sm">
              {emoji}
            </span>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: reverse ? -40 : 40 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.6, delay: 0.1 }}
          className="w-full lg:w-1/2"
        >
          <span className="inline-flex items-center gap-1.5 rounded-full bg-gradient-to-r from-sapphire-100 to-purple-100 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-purple-700 dark:from-sapphire-950/40 dark:to-purple-950/40 dark:text-purple-300">
            {emoji} {badge}
          </span>
          <p className="mt-4 border-l-4 border-purple-400/50 pl-4 text-xl font-medium leading-relaxed text-foreground sm:text-2xl">
            {quote}
          </p>
          <Link
            href={ctaHref}
            className="mt-5 inline-flex items-center gap-1.5 text-sm font-semibold text-sapphire-600 hover:text-purple-600"
          >
            {ctaLabel}
            <ArrowRight className="h-4 w-4" />
          </Link>
        </motion.div>
      </div>
    </section>
  );
}
