"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import type { LucideIcon } from "lucide-react";
import { ArrowRight } from "lucide-react";

interface ProductCardProps {
  href: string;
  icon: LucideIcon;
  eyebrow: string;
  emoji: string;
  title: string;
  audience: string;
  description: string;
  bullets: { emoji: string; text: string }[];
  delay: number;
}

// The blurred glow behind each card and the hover-lift are the "boxes" the
// animation lives on — kept blue-toned (sapphire) per the brief, rather
// than repeating the purple used for the page-wide aura, so the two read
// as distinct from the backdrop rather than blending into it. Colors here
// are fixed light-on-dark (not theme tokens) to match the homepage's fixed
// dark backdrop, rather than flipping to dark-on-dark in light mode.
export function ProductCard({
  href,
  icon: Icon,
  eyebrow,
  emoji,
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
      whileHover={{ y: -8 }}
      className="group relative flex-1"
    >
      <motion.div
        animate={{ opacity: [0.55, 0.9, 0.55] }}
        transition={{ duration: 6, repeat: Infinity, ease: "easeInOut", delay }}
        className="absolute -inset-5 -z-10 rounded-[2.5rem] bg-sapphire-400/35 blur-3xl transition-opacity duration-500 group-hover:opacity-100"
      />
      <Link
        href={href}
        className="block h-full rounded-[2rem] border border-white/10 bg-white/[0.06] p-10 shadow-2xl shadow-black/40 backdrop-blur-xl transition-colors duration-300 hover:border-sapphire-400/50 hover:bg-white/[0.09] sm:p-12"
      >
        <div className="flex h-full min-h-[30rem] flex-col gap-7">
          <div className="flex items-center justify-between">
            <span className="inline-flex h-14 w-14 items-center justify-center rounded-2xl bg-sapphire-500/20 text-sapphire-300">
              <Icon className="h-7 w-7" />
            </span>
            <span className="text-xs font-semibold uppercase tracking-[0.2em] text-white/50">
              {audience}
            </span>
          </div>

          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.25em] text-sapphire-300">
              {emoji} {eyebrow}
            </p>
            <h2 className="mt-2 text-4xl font-bold tracking-tight text-white sm:text-5xl">
              {title}
            </h2>
          </div>

          <p className="text-lg leading-relaxed text-white/70">{description}</p>

          <ul className="mt-auto flex flex-col gap-3.5 border-t border-white/10 pt-7">
            {bullets.map((bullet) => (
              <li key={bullet.text} className="flex items-start gap-3 text-base text-white/85">
                <span aria-hidden className="text-lg leading-none">
                  {bullet.emoji}
                </span>
                {bullet.text}
              </li>
            ))}
          </ul>

          <span className="inline-flex items-center gap-2 text-base font-semibold text-sapphire-300 transition-transform duration-300 group-hover:translate-x-1">
            Enter {eyebrow}
            <ArrowRight className="h-5 w-5" />
          </span>
        </div>
      </Link>
    </motion.div>
  );
}
