import { createLogoutFinishRoute } from "@hostflow/auth/src/routes/logout-finish";
import { createAuthConfig } from "@hostflow/auth/src/config";

export const GET = createLogoutFinishRoute(createAuthConfig("XANUOS", "/xanuos"));
