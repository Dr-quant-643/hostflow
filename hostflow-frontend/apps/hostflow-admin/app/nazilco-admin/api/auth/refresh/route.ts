import { createRefreshRoute } from "@hostflow/auth/src/routes/refresh";
import { createAuthConfig } from "@hostflow/auth/src/config";

export const POST = createRefreshRoute(createAuthConfig("NAZILCO_ADMIN", "/nazilco-admin"));
