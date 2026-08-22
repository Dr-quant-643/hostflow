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

// Mirrors module-identity's RegisterHostRequest -- self-service XanuOS
// signup, the property-manager counterpart to RegisterGuestRequest.
export interface RegisterHostRequest {
  organizationName: string;
  adminFirstName: string;
  adminLastName: string;
  adminEmail: string;
  password: string;
}
