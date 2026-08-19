import { z } from "zod";
import { nonEmptyString } from "../common";

export const createOrganizationSchema = z.object({
  name: nonEmptyString("Organization name", 150),
  slug: z
    .string()
    .trim()
    .min(2, { message: "Slug must be at least 2 characters." })
    .max(60)
    .regex(/^[a-z0-9]+(-[a-z0-9]+)*$/, {
      message: "Slug must be lowercase letters, numbers, and hyphens only.",
    }),
  primaryProduct: z.enum(["XANUOS", "NAZILCO"]),
});

export type CreateOrganizationFormValues = z.infer<
  typeof createOrganizationSchema
>;
