"use client";

import { motion } from "framer-motion";

// Ambient, blurred brand-color backdrop (sapphire/purple) — fixed behind
// page content so scrolling doesn't reveal flat white beneath it. Used on
// pages that otherwise read as a plain white dashboard.
export function BrandBackground() {
  return (
    <div className="pointer-events-none fixed inset-0 -z-10 overflow-hidden bg-background">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1.2, ease: "easeOut" }}
        className="absolute -left-40 -top-40 h-[32rem] w-[32rem] rounded-full bg-sapphire-400/25 blur-[120px] dark:bg-sapphire-500/15"
      />
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1.2, delay: 0.1, ease: "easeOut" }}
        className="absolute -right-32 top-1/4 h-[28rem] w-[28rem] rounded-full bg-purple-400/20 blur-[120px] dark:bg-purple-500/12"
      />
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1.2, delay: 0.2, ease: "easeOut" }}
        className="absolute bottom-0 left-1/4 h-[24rem] w-[24rem] rounded-full bg-sapphire-300/15 blur-[100px] dark:bg-sapphire-700/10"
      />
      <div
        className="absolute inset-0 opacity-[0.04]"
        style={{
          backgroundImage: "radial-gradient(currentColor 1px, transparent 1px)",
          backgroundSize: "24px 24px",
        }}
      />
    </div>
  );
}
