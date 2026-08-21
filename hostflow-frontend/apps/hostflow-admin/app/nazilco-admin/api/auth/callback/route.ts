import { createCallbackRoute } from "@hostflow/auth/src/routes/callback";
import { createAuthConfig } from "@hostflow/auth/src/config";

export const GET = createCallbackRoute(createAuthConfig("NAZILCO_ADMIN", "/nazilco-admin"));
