"use client";

import { motion } from "framer-motion";

// Ambient, continuously-drifting purple backdrop for the homepage — unlike
// BrandBackground (nazilco's dashboard chrome, a one-time fade-in), this
// stays gently in motion the whole time the picker is on screen, since
// it's the very first thing anyone sees of HostFlow.
export function HostflowAura() {
  return (
    <div className="pointer-events-none fixed inset-0 -z-10 overflow-hidden bg-background">
      <motion.div
        animate={{
          x: [0, 40, -20, 0],
          y: [0, -30, 20, 0],
          scale: [1, 1.08, 0.96, 1],
        }}
        transition={{ duration: 22, repeat: Infinity, ease: "easeInOut" }}
        className="absolute -left-48 -top-48 h-[36rem] w-[36rem] rounded-full bg-purple-500/25 blur-[130px] dark:bg-purple-500/20"
      />
      <motion.div
        animate={{
          x: [0, -30, 30, 0],
          y: [0, 25, -15, 0],
          scale: [1, 0.94, 1.1, 1],
        }}
        transition={{ duration: 26, repeat: Infinity, ease: "easeInOut", delay: 1 }}
        className="absolute -right-40 top-1/3 h-[32rem] w-[32rem] rounded-full bg-purple-400/20 blur-[130px] dark:bg-purple-400/15"
      />
      <motion.div
        animate={{
          x: [0, 25, -25, 0],
          y: [0, -20, 15, 0],
        }}
        transition={{ duration: 20, repeat: Infinity, ease: "easeInOut", delay: 0.5 }}
        className="absolute bottom-[-10rem] left-1/4 h-[28rem] w-[28rem] rounded-full bg-sapphire-400/15 blur-[120px] dark:bg-sapphire-500/12"
      />
      <div
        className="absolute inset-0 opacity-[0.035]"
        style={{
          backgroundImage: "radial-gradient(currentColor 1px, transparent 1px)",
          backgroundSize: "26px 26px",
        }}
      />
    </div>
  );
}
