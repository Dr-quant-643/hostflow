"use client";

import { motion } from "framer-motion";
import { Check } from "lucide-react";

const STEPS = ["Book", "Checkout", "Confirmed"] as const;

export function BookingSteps({ current }: { current: 0 | 1 | 2 }) {
  return (
    <div className="flex items-center justify-center gap-2">
      {STEPS.map((label, i) => (
        <div key={label} className="flex items-center gap-2">
          <div className="flex flex-col items-center gap-1">
            <div
              className={`flex h-7 w-7 items-center justify-center rounded-full text-xs font-semibold transition-colors duration-300 ${
                i <= current ? "bg-sapphire-600 text-white" : "bg-muted text-muted-foreground"
              }`}
            >
              {i < current ? <Check className="h-3.5 w-3.5" /> : i + 1}
            </div>
            <span className={`text-[11px] ${i <= current ? "font-medium text-foreground" : "text-muted-foreground"}`}>
              {label}
            </span>
          </div>
          {i < STEPS.length - 1 && (
            <div className="mb-4 h-0.5 w-8 overflow-hidden rounded-full bg-muted sm:w-16">
              <motion.div
                initial={false}
                animate={{ width: i < current ? "100%" : "0%" }}
                transition={{ duration: 0.4 }}
                className="h-full bg-sapphire-600"
              />
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
