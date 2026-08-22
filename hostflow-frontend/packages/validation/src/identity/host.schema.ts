import { z } from "zod";
import { nonEmptyString, emailSchema, passwordSchema } from "../common";

export const hostSignupFormSchema = z.object({
  organizationName: nonEmptyString("Business name", 150),
  adminFirstName: nonEmptyString("First name", 100),
  adminLastName: nonEmptyString("Last name", 100),
  adminEmail: emailSchema,
  password: passwordSchema,
});

export type HostSignupFormValues = z.infer<typeof hostSignupFormSchema>;
