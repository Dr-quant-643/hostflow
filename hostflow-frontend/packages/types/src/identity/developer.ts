// Mirrors module-identity's ApiKey / WebhookSubscription -- the foundation
// for the public API product mentioned in planning. No rate limiting/usage
// metering/billing yet; see ApiKey.java's own doc comment for why.

export interface ApiKeyResponse {
  id: string;
  name: string;
  keyPrefix: string;
  lastUsedAt: string | null;
  revoked: boolean;
}

// The ONLY response shape that carries the raw key -- shown once at
// creation, never retrievable again.
export interface CreateApiKeyResponse {
  id: string;
  name: string;
  rawKey: string;
}

export interface CreateApiKeyRequest {
  name: string;
}

export interface WebhookSubscriptionResponse {
  id: string;
  url: string;
  eventType: string;
  secret: string;
  active: boolean;
}

export interface CreateWebhookSubscriptionRequest {
  url: string;
  eventType: string;
}
