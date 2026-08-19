import type { Config } from "tailwindcss";
import {
  sapphire,
  purple,
  neutral,
  success,
  warning,
  error,
} from "./src/colors";
import { fontFamily, fontSize, fontWeight } from "./src/typography";
import { radius } from "./src/radius";
import { shadow } from "./src/shadows";
import { breakpoints } from "./src/breakpoints";
import { duration } from "./src/motion";
import { zIndex } from "./src/z-index";

// Shared Tailwind preset. Every app's tailwind.config.ts does:
//   import hostflowPreset from "@hostflow/theme/tailwind.preset";
//   export default { presets: [hostflowPreset], content: [...] };
const preset: Partial<Config> = {
  darkMode: "class",
  theme: {
    screens: breakpoints,
    extend: {
      colors: {
        sapphire,
        purple,
        neutral,
        // success/warning/error are raw scales (50/500/700) with no DEFAULT —
        // adding one here so bare `text-success`/`bg-success` (used by Badge's
        // success/warning variants) actually resolves to a color instead of
        // silently generating no utility class at all.
        success: { ...success, DEFAULT: success[500], foreground: "#FFFFFF" },
        warning: { ...warning, DEFAULT: warning[500], foreground: "#FFFFFF" },
        error: { ...error, DEFAULT: error[500], foreground: "#FFFFFF" },
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
      },
      fontFamily: fontFamily as any,
      fontSize: fontSize as any,
      fontWeight,
      borderRadius: radius,
      boxShadow: shadow,
      transitionDuration: duration,
      zIndex: Object.fromEntries(
        Object.entries(zIndex).map(([k, v]) => [k, String(v)]),
      ),
    },
  },
  plugins: [],
};

export default preset;
