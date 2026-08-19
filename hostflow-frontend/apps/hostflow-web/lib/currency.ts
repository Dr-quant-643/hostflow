// Kenya-first for now — every price is treated as KES (the real basePrice
// field is a currency-agnostic BigDecimal string; this is purely a
// display-layer choice until multi-currency support exists). Mirrors
// nazilco-web/lib/currency.ts so the same basePrice value renders
// identically to guests on NazilCo and to the owner managing it here.
const formatter = new Intl.NumberFormat("en-KE", {
  style: "currency",
  currency: "KES",
  maximumFractionDigits: 0,
});

export function formatKES(amount: string | number | null | undefined): string {
  if (amount == null) return "";
  const n = typeof amount === "string" ? Number(amount) : amount;
  if (Number.isNaN(n)) return "";
  return formatter.format(n);
}
