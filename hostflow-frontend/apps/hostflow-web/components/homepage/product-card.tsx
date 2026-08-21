"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import type { LucideIcon } from "lucide-react";
import { ArrowRight } from "lucide-react";

interface ProductCardProps {
  href: string;
  icon: LucideIcon;
  eyebrow: string;
  title: string;
  audience: string;
  description: string;
  bullets: string[];
  delay: number;
}

// The blurred glow behind each card and the hover-lift are the "boxes" the
// animation lives on — kept blue-toned (sapphire) per the brief, rather
// than repeating the purple used for the page-wide aura, so the two read
// as distinct from the backdrop rather than blending into it.
export function ProductCard({
  href,
  icon: Icon,
  eyebrow,
  title,
  audience,
  description,
  bullets,
  delay,
}: ProductCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6, delay, ease: "easeOut" }}
      whileHover={{ y: -6 }}
      className="group relative flex-1"
    >
      <motion.div
        animate={{ opacity: [0.5, 0.85, 0.5] }}
        transition={{ duration: 6, repeat: Infinity, ease: "easeInOut", delay }}
        className="absolute -inset-4 -z-10 rounded-[2rem] bg-sapphire-400/25 blur-3xl transition-opacity duration-500 group-hover:opacity-100 dark:bg-sapphire-500/20"
      />
      <Link
        href={href}
        className="block h-full rounded-3xl border border-white/10 bg-card/70 p-8 shadow-xl shadow-purple-950/5 backdrop-blur-xl transition-colors duration-300 hover:border-sapphire-400/40 sm:p-10"
      >
        <div className="flex h-full flex-col gap-6">
          <div className="flex items-center justify-between">
            <span className="inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-sapphire-500/15 text-sapphire-600 dark:text-sapphire-300">
              <Icon className="h-6 w-6" />
            </span>
            <span className="text-xs font-semibold uppercase tracking-[0.2em] text-muted-foreground">
              {audience}
            </span>
          </div>

          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-sapphire-600 dark:text-sapphire-300">
              {eyebrow}
            </p>
            <h2 className="mt-1 text-3xl font-bold tracking-tight sm:text-4xl">{title}</h2>
          </div>

          <p className="text-base leading-relaxed text-muted-foreground">{description}</p>

          <ul className="mt-auto flex flex-col gap-2.5 border-t border-border/60 pt-6">
            {bullets.map((bullet) => (
              <li key={bullet} className="flex items-start gap-2.5 text-sm text-foreground/90">
                <span className="mt-1.5 h-1.5 w-1.5 flex-shrink-0 rounded-full bg-sapphire-500" />
                {bullet}
              </li>
            ))}
          </ul>

          <span className="inline-flex items-center gap-1.5 text-sm font-semibold text-sapphire-600 transition-transform duration-300 group-hover:translate-x-1 dark:text-sapphire-300">
            Enter {eyebrow}
            <ArrowRight className="h-4 w-4" />
          </span>
        </div>
      </Link>
    </motion.div>
  );
}
