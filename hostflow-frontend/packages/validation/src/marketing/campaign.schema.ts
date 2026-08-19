import { z } from "zod";
import { uuidSchema, nonEmptyString } from "../common";

export const campaignFormSchema = z.object({
  propertyId: uuidSchema.optional(),
  name: nonEmptyString("Campaign name", 150),
  platform: z.enum([
    "FACEBOOK",
    "INSTAGRAM",
    "TIKTOK",
    "WHATSAPP",
    "EMAIL",
    "GOOGLE_ADS",
    "BLOG",
  ]),
  content: nonEmptyString("Content", 5000),
});

export type CampaignFormValues = z.infer<typeof campaignFormSchema>;
