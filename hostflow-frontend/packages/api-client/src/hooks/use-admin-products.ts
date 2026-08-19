import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";

// No backing entity exists in any of the 15 completed backend modules.
// This is a speculative shape based on what "Products admin" would
// plausibly mean for a SaaS platform (plan tiers XanuOS/NazilCo orgs
// subscribe to) — not derived from anything the backend actually exposes.
// Treat this whole file as a placeholder to delete or rewrite once the
// real concept is defined, not as groundwork worth preserving as-is.
export interface ProductPlan {
  id: string;
  name: string;
  productScope: "XANUOS" | "NAZILCO";
  monthlyPrice: string;
  activeOrgCount: number;
}

export function useProductPlans() {
  return useQuery({
    queryKey: ["admin", "products", "plans"],
    queryFn: () => api.get<ProductPlan[]>("/admin/products/plans"),
    retry: false, // no point retrying a route that likely 404s
  });
}
