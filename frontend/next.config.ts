import type { NextConfig } from "next";

const apiUrl = process.env.API_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/flows",
        destination: `${apiUrl}/flows`,
      },
      {
        source: "/api/flows/:path*",
        destination: `${apiUrl}/flows/:path*`,
      },
    ];
  },
};

export default nextConfig;
