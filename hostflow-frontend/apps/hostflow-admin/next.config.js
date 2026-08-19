/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  transpilePackages: [
    "@hostflow/theme",
    "@hostflow/ui",
    "@hostflow/types",
    "@hostflow/api-client",
    "@hostflow/auth",
    "@hostflow/validation",
  ],
  async rewrites() {
    return [
      {
        source: "/api/v1/:path*",
        destination: "http://localhost:8085/api/v1/:path*",
      },
    ];
  },
};

module.exports = nextConfig;
