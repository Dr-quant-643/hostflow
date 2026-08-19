import { z } from "zod";
import { nonEmptyString } from "../common";

export const ownerResponseFormSchema = z.object({
  response: nonEmptyString("Response", 2000),
});

export type OwnerResponseFormValues = z.infer<typeof ownerResponseFormSchema>;

export const guestReviewFormSchema = z.object({
  rating: z.number().int().min(1).max(5),
  comment: z.string().max(2000).optional(),
});

export type GuestReviewFormValues = z.infer<typeof guestReviewFormSchema>;
