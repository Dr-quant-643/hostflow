"use client";

import { motion } from "framer-motion";
import { PageHeader, Stack, Card, Badge, Skeleton, EmptyState } from "@hostflow/ui";
import { Receipt, CheckCircle2, RotateCcw, Clock } from "lucide-react";
import { useMyInvoices } from "@hostflow/api-client/src/hooks/use-guest-invoices";
import { formatKES } from "@/lib/currency";
import { BrandBackground } from "@/components/nazilco/layout/brand-background";

const STATUS_META: Record<string, { variant: "success" | "warning" | "secondary"; icon: typeof Receipt; label: string }> = {
  PAID: { variant: "success", icon: CheckCircle2, label: "Paid" },
  PENDING: { variant: "warning", icon: Clock, label: "Pending" },
  REFUNDED: { variant: "secondary", icon: RotateCcw, label: "Refunded" },
};

export default function InvoicesPage() {
  const { data, isLoading, isError } = useMyInvoices();

  const totalDue = (data ?? [])
    .filter((i) => i.status === "PENDING")
    .reduce((sum, i) => sum + Number(i.amount), 0);

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-3xl p-6">
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <PageHeader title="My Invoices" description="Payment history and receipts for your stays" />
        </motion.div>

        {!isLoading && data && data.length > 0 && (
          <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: 0.05 }}>
            <Card className="flex items-center justify-between bg-gradient-to-r from-sapphire-600 to-purple-600 p-5 text-white">
              <div>
                <p className="text-xs text-white/80">Total outstanding</p>
                <p className="text-2xl font-semibold">{formatKES(totalDue)}</p>
              </div>
              <Receipt className="h-8 w-8 text-white/70" />
            </Card>
          </motion.div>
        )}

        {isLoading && <Skeleton className="h-64 w-full" />}
        {!isLoading && isError && (
          <EmptyState title="Couldn't load your invoices" description="Try refreshing." />
        )}
        {!isLoading && data && data.length === 0 && (
          <EmptyState title="No invoices yet" description="Invoices appear here once you complete a booking." />
        )}
        {!isLoading && data && data.length > 0 && (
          <Stack gap="sm">
            {data.map((invoice, i) => {
              const meta = STATUS_META[invoice.status] ?? { variant: "secondary" as const, icon: Receipt, label: invoice.status };
              const Icon = meta.icon;
              return (
                <motion.div
                  key={invoice.id}
                  initial={{ opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.35, delay: i * 0.05 }}
                >
                  <Card className="flex items-center gap-4 p-4 transition-shadow hover:shadow-md">
                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-sapphire-50 text-sapphire-600">
                      <Icon className="h-5 w-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="font-medium">{formatKES(invoice.amount)}</p>
                      <p className="text-sm text-muted-foreground">
                        {invoice.status === "PENDING" ? "Due" : "Was due"} {invoice.dueDate}
                      </p>
                    </div>
                    <Badge variant={meta.variant}>{meta.label}</Badge>
                  </Card>
                </motion.div>
              );
            })}
          </Stack>
        )}
      </Stack>
    </>
  );
}
