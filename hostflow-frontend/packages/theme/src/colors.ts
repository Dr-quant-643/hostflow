// Raw color scales. Do not consume these directly in components —
// use the semantic tokens exported from semantic.ts instead.

export const sapphire = {
  50: "#EFF6FF",
  100: "#DBEAFE",
  200: "#BFDBFE",
  300: "#93C5FD",
  400: "#60A5FA",
  500: "#2563EB",
  600: "#1D4ED8",
  700: "#1E40AF",
  800: "#1E3A8A",
  900: "#172554",
} as const;

export const purple = {
  50: "#FAF5FF",
  100: "#F3E8FF",
  200: "#E9D5FF",
  300: "#D8B4FE",
  400: "#C084FC",
  500: "#A855F7",
  600: "#9333EA",
  700: "#7E22CE",
  800: "#6B21A8",
  900: "#581C87",
} as const;

export const neutral = {
  50: "#F8FAFC",
  100: "#F1F5F9",
  200: "#E2E8F0",
  300: "#CBD5E1",
  400: "#94A3B8",
  500: "#64748B",
  600: "#475569",
  700: "#334155",
  800: "#1E293B",
  900: "#0F172A",
  950: "#020617",
} as const;

export const success = {
  50: "#F0FDF4",
  500: "#22C55E",
  700: "#15803D",
} as const;

export const warning = {
  50: "#FFFBEB",
  500: "#F59E0B",
  700: "#B45309",
} as const;

export const error = {
  50: "#FEF2F2",
  500: "#EF4444",
  700: "#B91C1C",
} as const;

export type ColorScale = typeof sapphire;
