// Mirrors module-identity's OrgUserSummaryResponse / UpdateUserRolesRequest.

export type UserRole = "XANUOS_OWNER" | "XANUOS_MANAGER" | "XANUOS_STAFF" | "NAZILCO_CUSTOMER" | "PLATFORM_ADMIN";

export interface OrgUserSummaryResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: UserRole[];
  active: boolean;
}

export interface UpdateUserRolesRequest {
  roles: UserRole[];
}
