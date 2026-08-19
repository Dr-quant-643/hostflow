import { z } from "zod";
import { nonEmptyString, emailSchema } from "../common";

export const contactFormSchema = z.object({
  fullName: nonEmptyString("Full name", 150),
  email: emailSchema.optional().or(z.literal("")),
  phone: z
    .string()
    .regex(/^\+?[0-9\s-]{7,20}$/, { message: "Enter a valid phone number." })
    .optional()
    .or(z.literal("")),
  source: z.string().max(100).optional(),
});

export type ContactFormValues = z.infer<typeof contactFormSchema>;

// SYSTEM_EVENT/SUPPORT_REQUEST are backend-generated only, not user-selectable
// from this form.
export const logInteractionSchema = z.object({
  type: z.enum(["CALL", "EMAIL", "MEETING", "NOTE", "WHATSAPP_MESSAGE"]),
  notes: nonEmptyString("Notes", 2000),
});

export type LogInteractionFormValues = z.infer<typeof logInteractionSchema>;
