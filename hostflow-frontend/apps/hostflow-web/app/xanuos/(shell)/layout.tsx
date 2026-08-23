import { Stack } from "@hostflow/ui";
import { AppSidebar } from "@/components/xanuos/app-sidebar";
import { AppTopbar } from "@/components/xanuos/app-topbar";

export default function ShellLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex h-screen w-full overflow-hidden">
      <AppSidebar />
      <div className="flex flex-1 flex-col overflow-hidden">
        <AppTopbar />
        <main className="flex-1 overflow-y-auto bg-gradient-to-br from-sapphire-50/40 via-background to-purple-50/30 p-6 dark:from-sapphire-950/10 dark:via-background dark:to-purple-950/10">
          <Stack gap="lg">{children}</Stack>
        </main>
      </div>
    </div>
  );
}
