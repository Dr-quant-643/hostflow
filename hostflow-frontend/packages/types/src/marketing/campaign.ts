// Mirrors module-marketing's CampaignResponse / CreateCampaignRequest. AI
// content generation was fully removed from the backend — a campaign is now
// just a planning record with manually-written content (DRAFT -> PUBLISHED
// -> ARCHIVED).

export type ContentPlatform =
  | "FACEBOOK"
  | "INSTAGRAM"
  | "TIKTOK"
  | "WHATSAPP"
  | "EMAIL"
  | "GOOGLE_ADS"
  | "BLOG";

export type CampaignStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";

export interface Campaign {
  id: string;
  propertyId?: string;
  name: string;
  platform: ContentPlatform;
  content: string;
  status: CampaignStatus;
}

export interface CreateCampaignRequest {
  propertyId?: string;
  name: string;
  platform: ContentPlatform;
  content: string;
}
