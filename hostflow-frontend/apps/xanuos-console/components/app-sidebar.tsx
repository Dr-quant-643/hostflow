"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Sidebar } from "@hostflow/ui";
import { CONSOLE_NAV } from "@/lib/nav-config";

export function AppSidebar() {
  const pathname = usePathname();

  return (
    <Sidebar
      LinkComponent={Link}
      items={CONSOLE_NAV.map((item) => {
        const Icon = item.icon;
        return {
          ...item,
          icon: <Icon className="h-4 w-4" />,
          active: pathname.startsWith(item.href),
        };
      })}
      brand="XanuOS Console"
    />
  );
}
