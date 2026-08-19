"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import Papa from "papaparse";
import {
  batchCreateInvoicesSchema,
  type BatchCreateInvoicesValues,
} from "@hostflow/validation";
import { Button, Stack, Alert } from "@hostflow/ui";
import { useBatchCreateInvoices } from "@hostflow/api-client/src/hooks/use-billing";
import type { BatchCreateInvoicesResponse } from "@hostflow/types";

export function BatchInvoiceImport() {
  const router = useRouter();
  const batchCreate = useBatchCreateInvoices();
  const [parseError, setParseError] = useState<string | null>(null);
  const [rowCount, setRowCount] = useState(0);
  const [parsed, setParsed] = useState<BatchCreateInvoicesValues | null>(null);
  const [result, setResult] = useState<BatchCreateInvoicesResponse | null>(null);

  const handleFile = (file: File) => {
    setParseError(null);
    Papa.parse(file, {
      header: true,
      skipEmptyLines: true,
      complete: (result) => {
        const candidate = { invoices: result.data };
        const validation = batchCreateInvoicesSchema.safeParse(candidate);
        if (!validation.success) {
          // Row cap (100) enforced client-side here, mirroring the backend's
          // sync-only path per Open Item H.
          setParseError(
            validation.error.issues[0]?.message ?? "Invalid CSV format",
          );
          setParsed(null);
          setRowCount(0);
          return;
        }
        setParsed(validation.data);
        setRowCount(validation.data.invoices.length);
      },
    });
  };

  return (
    <Stack gap="md">
      <input
        type="file"
        accept=".csv"
        onChange={(e) => {
          const file = e.target.files?.[0];
          if (file) handleFile(file);
        }}
      />
      {parseError && <Alert variant="error">{parseError}</Alert>}
      {rowCount > 0 && !parseError && (
        <Alert variant="info">{rowCount} rows ready to import (max 100).</Alert>
      )}
      <Button
        disabled={!parsed}
        loading={batchCreate.isPending}
        onClick={async () => {
          if (!parsed) return;
          try {
            const response = await batchCreate.mutateAsync(parsed);
            setResult(response);
            if (response.failed === 0) {
              toast.success(`${response.succeeded} invoices created`);
              router.push("/billing");
            } else {
              toast.error(`${response.succeeded} created, ${response.failed} failed`);
            }
          } catch {
            toast.error("Batch import failed");
          }
        }}
      >
        Import
      </Button>
      {result && result.failed > 0 && (
        <Stack gap="sm">
          {result.results
            .filter((r) => !r.success)
            .map((r) => (
              <Alert key={r.index} variant="error">
                Row {r.index + 1}: {r.errorMessage}
              </Alert>
            ))}
        </Stack>
      )}
    </Stack>
  );
}
