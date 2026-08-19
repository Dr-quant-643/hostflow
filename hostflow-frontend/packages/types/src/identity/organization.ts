// Mirrors module-identity's OrganizationResponse.

export interface OrganizationResponse {
  id: string;
  name: string;
  slug: string;
  primaryProduct: "XANUOS" | "NAZILCO";
  active: boolean;
}

export interface CreateOrganizationRequest {
  name: string;
  slug: string;
  primaryProduct: "XANUOS" | "NAZILCO";
}

export interface RenameOrganizationRequest {
  name: string;
}
