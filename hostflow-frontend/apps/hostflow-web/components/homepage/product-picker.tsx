import Link from "next/link";
import { Button, Stack } from "@hostflow/ui";

// Plain, unauthenticated landing page. Just links to each product's root —
// it does not check auth itself. Landing on /xanuos or /nazilco with no
// session is what triggers that branch's middleware redirect to its own
// login route.
export function ProductPicker() {
  return (
    <Stack gap="lg" align="center" className="py-24 text-center">
      <h1 className="text-4xl font-semibold">Welcome to HostFlow</h1>
      <p className="text-muted-foreground">Choose your workspace</p>
      <Stack direction="row" gap="md">
        <Button asChild size="lg">
          <Link href="/xanuos">XanuOS — Property Management</Link>
        </Button>
        <Button asChild size="lg" variant="outline">
          <Link href="/nazilco">NazilCo — Find a Stay</Link>
        </Button>
      </Stack>
    </Stack>
  );
}
