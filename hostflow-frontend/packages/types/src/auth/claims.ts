// JWT claims issued by Keycloak and consumed by the frontend (packages/auth,
// Module 6) to drive UI-level authorization. Mirrors core-security's
// JWT→authorities converter.

export type ProductScope = "PRODUCT_XANUOS" | "PRODUCT_NAZILCO";

export type Authority =
  | ProductScope
  | "ROLE_PLATFORM_ADMIN"
  | "ROLE_ORG_ADMIN"
  | "ROLE_STAFF"
  | "ROLE_GUEST";

export interface AuthClaims {
  sub: string;
  tenant_id: string;
  product_scope: ProductScope[];
  authorities: Authority[];
  email?: string;
  name?: string;
  exp: number;
  iat: number;
}

export interface SessionUser {
  id: string;
  tenantId: string;
  email?: string;
  name?: string;
  authorities: Authority[];
  productScope: ProductScope[];
}
