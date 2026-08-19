import { sapphire, purple, neutral, success, warning, error } from "./colors";

// Semantic tokens: what components should actually reference.
// Keeps light/dark and brand swaps isolated from color.ts.

export const semanticLight = {
  background: neutral[50],
  foreground: neutral[900],
  card: "#FFFFFF",
  cardForeground: neutral[900],
  border: neutral[200],
  input: neutral[200],
  muted: neutral[100],
  mutedForeground: neutral[500],
  primary: sapphire[500],
  primaryForeground: "#FFFFFF",
  accent: purple[500],
  accentForeground: "#FFFFFF",
  success: success[500],
  warning: warning[500],
  destructive: error[500],
  destructiveForeground: "#FFFFFF",
  ring: sapphire[500],
} as const;

export const semanticDark = {
  background: neutral[950],
  foreground: neutral[50],
  card: neutral[900],
  cardForeground: neutral[50],
  border: neutral[800],
  input: neutral[800],
  muted: neutral[800],
  mutedForeground: neutral[400],
  primary: sapphire[400],
  primaryForeground: neutral[950],
  accent: purple[400],
  accentForeground: neutral[950],
  success: success[500],
  warning: warning[500],
  destructive: error[500],
  destructiveForeground: "#FFFFFF",
  ring: sapphire[400],
} as const;

export type SemanticTokens = typeof semanticLight;
