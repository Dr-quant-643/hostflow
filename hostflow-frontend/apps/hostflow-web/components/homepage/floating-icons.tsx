"use client";

import { motion } from "framer-motion";

// Purely decorative — scattered, low-opacity emoji that slowly drift and
// bob, so the space around the two product cards doesn't read as empty.
// Mix of travel (NazilCo) and property (XanuOS) motifs, since this page
// introduces both.
const ICONS = [
  { emoji: "🏡", top: "12%", left: "8%", size: "3rem", duration: 9, delay: 0 },
  { emoji: "🧳", top: "70%", left: "6%", size: "2.5rem", duration: 11, delay: 0.6 },
  { emoji: "🌍", top: "18%", left: "88%", size: "2.75rem", duration: 10, delay: 0.3 },
  { emoji: "🔑", top: "62%", left: "90%", size: "2.25rem", duration: 8, delay: 1 },
  { emoji: "🏢", top: "82%", left: "20%", size: "2.5rem", duration: 12, delay: 0.8 },
  { emoji: "✈️", top: "8%", left: "45%", size: "2rem", duration: 9.5, delay: 0.4 },
  { emoji: "🛎️", top: "85%", left: "72%", size: "2.25rem", duration: 10.5, delay: 0.2 },
] as const;

export function FloatingIcons() {
  return (
    <div className="pointer-events-none absolute inset-0 -z-10 overflow-hidden">
      {ICONS.map((icon, i) => (
        <motion.span
          key={i}
          aria-hidden
          className="absolute select-none opacity-[0.14] blur-[0.5px]"
          style={{ top: icon.top, left: icon.left, fontSize: icon.size }}
          animate={{
            y: [0, -18, 0, 14, 0],
            rotate: [0, 6, 0, -6, 0],
          }}
          transition={{
            duration: icon.duration,
            delay: icon.delay,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        >
          {icon.emoji}
        </motion.span>
      ))}
    </div>
  );
}
