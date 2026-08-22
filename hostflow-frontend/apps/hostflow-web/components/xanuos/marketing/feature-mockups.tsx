"use client";

import { motion } from "framer-motion";

export function PortfolioMockup() {
  const rows = [
    { name: "Kilimani Heights", units: 24, occ: 96, rent: "KES 2.4M" },
    { name: "Westlands Court", units: 18, occ: 88, rent: "KES 1.8M" },
    { name: "Karen Gardens", units: 40, occ: 94, rent: "KES 3.1M" },
  ];
  return (
    <div className="space-y-2 text-xs">
      <div className="flex items-center justify-between text-muted-foreground">
        <span className="flex items-center gap-1.5">
          <motion.span
            animate={{ opacity: [1, 0.3, 1] }}
            transition={{ duration: 1.6, repeat: Infinity }}
            className="h-1.5 w-1.5 rounded-full bg-emerald-500"
          />
          Live portfolio
        </span>
        <span>82 units total</span>
      </div>
      {rows.map((r, i) => (
        <motion.div
          key={r.name}
          initial={{ opacity: 0, x: -8 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.1 }}
          className="flex items-center justify-between rounded-lg border border-border/50 bg-card px-3 py-2"
        >
          <div>
            <p className="font-medium text-foreground">{r.name}</p>
            <p className="text-muted-foreground">
              {r.units} units · {r.rent}/mo
            </p>
          </div>
          <span
            className={`rounded-full px-2 py-0.5 text-[10px] font-semibold ${
              r.occ >= 90 ? "bg-emerald-100 text-emerald-700" : "bg-amber-100 text-amber-700"
            }`}
          >
            {r.occ}% occupied
          </span>
        </motion.div>
      ))}
    </div>
  );
}

export function BillingMockup() {
  const invoices = [
    { id: "INV-2044", tenant: "Jane W.", amount: "KES 45,000", status: "Paid" },
    { id: "INV-2045", tenant: "Mercy K.", amount: "KES 38,000", status: "Overdue" },
    { id: "INV-2046", tenant: "Brian O.", amount: "KES 52,000", status: "Paid" },
  ];
  return (
    <div className="space-y-2 text-xs">
      <div className="rounded-lg bg-gradient-to-r from-sapphire-500/10 to-purple-500/10 p-3">
        <p className="text-muted-foreground">This month</p>
        <p className="text-lg font-semibold text-foreground">KES 2.4M collected</p>
      </div>
      {invoices.map((inv, i) => (
        <motion.div
          key={inv.id}
          initial={{ opacity: 0, y: 6 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.1 }}
          className="flex items-center justify-between rounded-lg border border-border/50 px-3 py-2"
        >
          <div>
            <p className="font-medium text-foreground">
              {inv.id} · {inv.tenant}
            </p>
            <p className="text-muted-foreground">{inv.amount}</p>
          </div>
          <span
            className={`rounded-full px-2 py-0.5 text-[10px] font-semibold ${
              inv.status === "Paid" ? "bg-emerald-100 text-emerald-700" : "bg-red-100 text-red-700"
            }`}
          >
            {inv.status === "Paid" ? "✓ Paid" : "⚠ Overdue"}
          </span>
        </motion.div>
      ))}
    </div>
  );
}

export function LeasingMockup() {
  const leases = [
    { tenant: "Amina H.", unit: "Kilimani Heights 4B", daysLeft: 12, progress: 92 },
    { tenant: "David M.", unit: "Westlands Court 2A", daysLeft: 58, progress: 60 },
  ];
  return (
    <div className="space-y-3 text-xs">
      {leases.map((l, i) => (
        <motion.div
          key={l.tenant}
          initial={{ opacity: 0, y: 6 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.12 }}
          className="rounded-lg border border-border/50 p-3"
        >
          <div className="flex items-center justify-between">
            <p className="font-medium text-foreground">
              {l.tenant} · {l.unit}
            </p>
            <span
              className={`rounded-full px-2 py-0.5 text-[10px] font-semibold ${
                l.daysLeft < 30 ? "bg-amber-100 text-amber-700" : "bg-sapphire-100 text-sapphire-700"
              }`}
            >
              Renews in {l.daysLeft}d
            </span>
          </div>
          <div className="mt-2 h-1.5 w-full overflow-hidden rounded-full bg-muted">
            <motion.div
              initial={{ width: 0 }}
              whileInView={{ width: `${l.progress}%` }}
              viewport={{ once: true }}
              transition={{ duration: 0.8 }}
              className="h-full rounded-full bg-gradient-to-r from-sapphire-500 to-purple-500"
            />
          </div>
        </motion.div>
      ))}
    </div>
  );
}

export function MaintenanceMockup() {
  const columns = [
    { label: "New", count: 3, items: ["Leaking tap · 4B"], color: "bg-red-100 text-red-700" },
    { label: "In Progress", count: 2, items: ["AC repair · 12A"], color: "bg-amber-100 text-amber-700" },
    { label: "Done", count: 8, items: ["Painting · 3C"], color: "bg-emerald-100 text-emerald-700" },
  ];
  return (
    <div className="grid grid-cols-3 gap-2 text-[11px]">
      {columns.map((col, i) => (
        <motion.div
          key={col.label}
          initial={{ opacity: 0, y: 8 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.1 }}
          className="rounded-lg border border-border/50 p-2"
        >
          <p className={`mb-1.5 inline-block rounded-full px-1.5 py-0.5 font-semibold ${col.color}`}>
            {col.label} ({col.count})
          </p>
          {col.items.map((it) => (
            <div key={it} className="mt-1 rounded-md bg-muted/60 p-1.5 text-foreground">
              {it}
            </div>
          ))}
        </motion.div>
      ))}
    </div>
  );
}

export function CrmMockup() {
  const stages = [
    { label: "New Lead", people: ["Faith N."] },
    { label: "Viewing", people: ["Peter K."] },
    { label: "Signed", people: ["Grace W."] },
  ];
  return (
    <div className="grid grid-cols-3 gap-2 text-[11px]">
      {stages.map((s, i) => (
        <motion.div
          key={s.label}
          initial={{ opacity: 0, scale: 0.95 }}
          whileInView={{ opacity: 1, scale: 1 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.1 }}
          className="rounded-lg border border-border/50 p-2"
        >
          <p className="mb-1.5 font-semibold text-muted-foreground">{s.label}</p>
          {s.people.map((p) => (
            <div key={p} className="mt-1 flex items-center gap-1.5 rounded-md bg-muted/60 p-1.5">
              <span className="flex h-4 w-4 items-center justify-center rounded-full bg-gradient-to-br from-sapphire-500 to-purple-500 text-[8px] font-bold text-white">
                {p[0]}
              </span>
              {p}
            </div>
          ))}
        </motion.div>
      ))}
    </div>
  );
}

export function AnalyticsMockup() {
  const bars = [40, 65, 50, 80, 70, 95];
  return (
    <div className="space-y-3">
      <div className="grid grid-cols-2 gap-2 text-xs">
        <div className="rounded-lg bg-gradient-to-br from-sapphire-500/10 to-sapphire-500/5 p-2">
          <p className="text-muted-foreground">Occupancy</p>
          <p className="text-base font-semibold text-foreground">
            92% <span className="text-[10px] text-emerald-600">↑3%</span>
          </p>
        </div>
        <div className="rounded-lg bg-gradient-to-br from-purple-500/10 to-purple-500/5 p-2">
          <p className="text-muted-foreground">Revenue</p>
          <p className="text-base font-semibold text-foreground">
            KES 8.4M <span className="text-[10px] text-emerald-600">↑</span>
          </p>
        </div>
      </div>
      <div className="flex h-20 items-end gap-1.5">
        {bars.map((h, i) => (
          <motion.div
            key={i}
            initial={{ height: 0 }}
            whileInView={{ height: `${h}%` }}
            viewport={{ once: true }}
            transition={{ delay: i * 0.08, duration: 0.6 }}
            className="flex-1 rounded-t-md bg-gradient-to-t from-sapphire-500 to-purple-500"
          />
        ))}
      </div>
    </div>
  );
}

export function MallMockup() {
  return (
    <div className="space-y-2 text-xs">
      {["Zara · Fashion", "Java House · F&B", "Naivas · Supermarket"].map((s, i) => (
        <motion.div
          key={s}
          initial={{ opacity: 0, x: -6 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.1 }}
          className="rounded-lg border border-border/50 px-3 py-2 text-foreground"
        >
          {s}
        </motion.div>
      ))}
      <div className="rounded-lg bg-muted/50 p-2">
        <div className="flex justify-between text-muted-foreground">
          <span>Parking</span>
          <span>340/500 bays</span>
        </div>
        <div className="mt-1 h-1.5 w-full overflow-hidden rounded-full bg-muted">
          <motion.div
            initial={{ width: 0 }}
            whileInView={{ width: "68%" }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="h-full rounded-full bg-gradient-to-r from-sapphire-500 to-purple-500"
          />
        </div>
      </div>
    </div>
  );
}

export function OfficeMockup() {
  return (
    <div className="space-y-2 text-xs">
      <p className="font-medium text-muted-foreground">Boardroom A · Today</p>
      <div className="flex gap-1">
        {["9am", "10am", "11am", "12pm", "1pm"].map((t, i) => (
          <motion.div
            key={t}
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ delay: i * 0.06 }}
            className={`flex-1 rounded-md py-1.5 text-center text-[10px] ${
              i === 1
                ? "bg-gradient-to-r from-sapphire-500 to-purple-500 text-white"
                : "bg-muted/60 text-muted-foreground"
            }`}
          >
            {t}
          </motion.div>
        ))}
      </div>
      <div className="rounded-lg border border-border/50 px-3 py-2">
        <p className="text-foreground">✅ Sarah M. checked in · Visiting Finance</p>
      </div>
    </div>
  );
}

export function MarketingMockup() {
  const campaigns = [
    { name: "Karen Gardens Launch", views: "1,240", inquiries: 38 },
    { name: "Westlands Vacancy Push", views: "820", inquiries: 21 },
  ];
  return (
    <div className="space-y-2 text-xs">
      {campaigns.map((c, i) => (
        <motion.div
          key={c.name}
          initial={{ opacity: 0, y: 6 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: i * 0.1 }}
          className="rounded-lg border border-border/50 p-3"
        >
          <p className="font-medium text-foreground">{c.name}</p>
          <div className="mt-1 flex items-center gap-3 text-muted-foreground">
            <span>👁️ {c.views} views</span>
            <span>📩 {c.inquiries} inquiries</span>
          </div>
        </motion.div>
      ))}
    </div>
  );
}
