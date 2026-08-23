import { createGatewayProxyRoute } from "@hostflow/auth/src/routes/gateway-proxy";
import { createAuthConfig } from "@hostflow/auth/src/config";

const handler = createGatewayProxyRoute(
  createAuthConfig("XANUOS", "/xanuos"),
  process.env.GATEWAY_BASE_URL ?? "http://localhost:8085",
);

export { handler as GET, handler as POST, handler as PATCH, handler as PUT, handler as DELETE };
