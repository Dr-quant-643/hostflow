import { z } from "zod";
import { nonEmptyString, emailSchema } from "../common";

export const guestSignupFormSchema = z.object({
  firstName: nonEmptyString("First name", 100),
  lastName: nonEmptyString("Last name", 100),
  email: emailSchema,
  phone: z.string().max(30).optional(),
});

export type GuestSignupFormValues = z.infer<typeof guestSignupFormSchema>;
