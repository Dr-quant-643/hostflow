"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Button, Input, Stack, Card, Badge, Skeleton, EmptyState } from "@hostflow/ui";
import {
  usePaymentsByInvoice,
  useRecordPayment,
  useMarkPaymentSucceeded,
  useMarkPaymentFailed,
  useRefundPayment,
} from "@hostflow/api-client/src/hooks/use-billing";

export function PaymentSection({ invoiceId }: { invoiceId: string }) {
  const { data: payments, isLoading } = usePaymentsByInvoice(invoiceId);
  const recordPayment = useRecordPayment();
  const markSucceeded = useMarkPaymentSucceeded(invoiceId);
  const markFailed = useMarkPaymentFailed(invoiceId);
  const refund = useRefundPayment(invoiceId);

  const [amount, setAmount] = useState("0.00");
  const [providerReference, setProviderReference] = useState("");

  return (
    <Card>
      <Stack gap="md">
        <h3 className="font-medium">Payments</h3>

        <Stack direction="row" gap="sm" align="end">
          <Input label="Amount" value={amount} onChange={(e) => setAmount(e.target.value)} />
          <Input
            label="Provider Reference"
            value={providerReference}
            onChange={(e) => setProviderReference(e.target.value)}
          />
          <Button
            loading={recordPayment.isPending}
            onClick={async () => {
              try {
                await recordPayment.mutateAsync({
                  invoiceId,
                  amount,
                  providerReference: providerReference || undefined,
                });
                toast.success("Payment recorded");
                setAmount("0.00");
                setProviderReference("");
              } catch {
                toast.error("Failed to record payment");
              }
            }}
          >
            Record Payment
          </Button>
        </Stack>

        {isLoading && <Skeleton className="h-24 w-full" />}
        {!isLoading && (!payments || payments.length === 0) && (
          <EmptyState title="No payments recorded yet" />
        )}
        {!isLoading &&
          payments &&
          payments.map((payment) => (
            <Stack key={payment.id} direction="row" justify="between" align="center">
              <Stack direction="row" gap="sm" align="center">
                <Badge>{payment.status}</Badge>
                <span className="text-sm">${payment.amount}</span>
                {payment.providerReference && (
                  <span className="text-xs text-muted-foreground">
                    {payment.providerReference}
                  </span>
                )}
              </Stack>
              {payment.status === "PENDING" && (
                <Stack direction="row" gap="sm">
                  <Button
                    size="sm"
                    loading={markSucceeded.isPending}
                    onClick={async () => {
                      try {
                        await markSucceeded.mutateAsync(payment.id);
                        toast.success("Payment marked succeeded");
                      } catch {
                        toast.error("Failed to update payment");
                      }
                    }}
                  >
                    Succeed
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    loading={markFailed.isPending}
                    onClick={async () => {
                      try {
                        await markFailed.mutateAsync(payment.id);
                        toast.success("Payment marked failed");
                      } catch {
                        toast.error("Failed to update payment");
                      }
                    }}
                  >
                    Fail
                  </Button>
                </Stack>
              )}
              {payment.status === "SUCCEEDED" && (
                <Button
                  size="sm"
                  variant="outline"
                  loading={refund.isPending}
                  onClick={async () => {
                    try {
                      await refund.mutateAsync(payment.id);
                      toast.success("Payment refunded");
                    } catch {
                      toast.error("Failed to refund payment");
                    }
                  }}
                >
                  Refund
                </Button>
              )}
            </Stack>
          ))}
      </Stack>
    </Card>
  );
}
