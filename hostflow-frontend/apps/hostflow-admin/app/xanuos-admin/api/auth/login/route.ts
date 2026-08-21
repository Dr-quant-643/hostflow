import { createLoginRoute } from "@hostflow/auth/src/routes/login";
import { createAuthConfig } from "@hostflow/auth/src/config";

export const GET = createLoginRoute(createAuthConfig("XANUOS_ADMIN", "/xanuos-admin"));
