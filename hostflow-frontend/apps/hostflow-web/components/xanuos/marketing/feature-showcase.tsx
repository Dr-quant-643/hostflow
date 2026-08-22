"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { ArrowRight } from "lucide-react";
import { BrowserMockup } from "./browser-mockup";

export function FeatureShowcase({
  reverse,
  emoji,
  badge,
  title,
  quote,
  description,
  photo,
  photoAlt,
  mockupPath,
  mockup,
  ctaHref,
  ctaLabel,
}: {
  reverse?: boolean;
  emoji: string;
  badge: string;
  title: string;
  quote: string;
  description: string;
  photo: string;
  photoAlt: string;
  mockupPath: string;
  mockup: React.ReactNode;
  ctaHref: string;
  ctaLabel: string;
}) {
  return (
    <section className="mx-auto max-w-6xl px-6 py-14">
      <div className={`flex flex-col items-center gap-12 lg:flex-row ${reverse ? "lg:flex-row-reverse" : ""}`}>
        <motion.div
          initial={{ opacity: 0, x: reverse ? 40 : -40 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.6 }}
          className="relative w-full pb-6 pr-6 lg:w-1/2"
        >
          <BrowserMockup path={mockupPath}>{mockup}</BrowserMockup>
          <div className="absolute -bottom-2 -right-2 h-24 w-24 rotate-3 overflow-hidden rounded-2xl border-4 border-background shadow-xl transition-transform hover:rotate-0">
            <img src={photo} alt={photoAlt} className="h-full w-full object-cover" />
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
          <h3 className="mt-3 text-2xl font-semibold text-foreground">{title}</h3>
          <p className="mt-3 border-l-4 border-purple-400/50 pl-4 text-base italic leading-relaxed text-foreground/90">
            &ldquo;{quote}&rdquo;
          </p>
          <p className="mt-3 text-muted-foreground">{description}</p>
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
