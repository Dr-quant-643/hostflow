import Link from "next/link";
import { Button, Stack } from "@hostflow/ui";

export function AdminPicker() {
  return (
    <Stack gap="lg" align="center" className="py-24 text-center">
      <h1 className="text-4xl font-semibold">RvanaFlow Admin</h1>
      <p className="text-muted-foreground">Choose your workspace</p>
      <Stack direction="row" gap="md">
        <Button asChild size="lg">
          <Link href="/xanuos-admin">XanuOS Admin</Link>
        </Button>
        <Button asChild size="lg" variant="outline">
          <Link href="/nazilco-admin">NazilCo Admin</Link>
        </Button>
      </Stack>
    </Stack>
  );
}
