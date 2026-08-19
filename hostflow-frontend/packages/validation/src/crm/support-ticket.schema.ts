import { z } from "zod";
import { nonEmptyString } from "../common";

export const raiseTicketFormSchema = z.object({
  subject: nonEmptyString("Subject", 200),
  description: z.string().max(2000).optional(),
  priority: z.enum(["LOW", "MEDIUM", "HIGH", "URGENT"]),
});

export type RaiseTicketFormValues = z.infer<typeof raiseTicketFormSchema>;
