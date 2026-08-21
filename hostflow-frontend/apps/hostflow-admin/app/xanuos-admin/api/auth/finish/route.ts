import { createFinishRoute } from "@hostflow/auth/src/routes/finish";
import { createAuthConfig } from "@hostflow/auth/src/config";

export const GET = createFinishRoute(createAuthConfig("XANUOS_ADMIN", "/xanuos-admin"));
