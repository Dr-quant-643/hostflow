import { createLogoutRoute } from "@hostflow/auth/src/routes/logout";
import { createAuthConfig } from "@hostflow/auth/src/config";

export const POST = createLogoutRoute(createAuthConfig("XANUOS_ADMIN", "/xanuos-admin"));
