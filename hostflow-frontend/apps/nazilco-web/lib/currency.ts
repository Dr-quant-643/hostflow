// NazilCo is Kenya-first for now — every price in the demo data/UI is
// treated as KES (the real basePrice/totalPrice fields are currency-agnostic
// BigDecimal strings; this is purely a display-layer choice until multi-
// currency support exists).
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
