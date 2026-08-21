import { z } from "zod";

// Shared primitives reused across every domain schema below — keeps error
// messages consistent and gives one place to tighten a rule later.

export const uuidSchema = z.string().uuid({ message: "Invalid identifier." });

// Backend serializes BigDecimal as a string; validate it's a numeric string
// with at most 2 decimal places rather than coercing to JS number anywhere.
export const decimalStringSchema = z
  .string()
  .regex(/^\d+(\.\d{1,2})?$/, {
    message: "Enter a valid amount, e.g. 1500.00",
  });

export const isoDateSchema = z
  .string()
  .regex(/^\d{4}-\d{2}-\d{2}$/, { message: "Use format YYYY-MM-DD" });

export const emailSchema = z
  .string()
  .email({ message: "Enter a valid email address." });

export const passwordSchema = z
  .string()
  .min(8, { message: "Password must be at least 8 characters." })
  .max(255);

export const nonEmptyString = (fieldLabel: string, max = 255) =>
  z
    .string()
    .trim()
    .min(1, { message: `${fieldLabel} is required.` })
    .max(max, { message: `${fieldLabel} must be ${max} characters or fewer.` });
