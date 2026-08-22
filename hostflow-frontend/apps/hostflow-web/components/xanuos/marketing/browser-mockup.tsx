export function BrowserMockup({ path, children }: { path: string; children: React.ReactNode }) {
  return (
    <div className="overflow-hidden rounded-2xl border border-border/60 bg-card shadow-2xl shadow-purple-500/10">
      <div className="flex items-center gap-1.5 border-b border-border/60 bg-muted/40 px-4 py-2.5">
        <span className="h-2.5 w-2.5 rounded-full bg-red-400" />
        <span className="h-2.5 w-2.5 rounded-full bg-amber-400" />
        <span className="h-2.5 w-2.5 rounded-full bg-green-400" />
        <div className="ml-3 flex-1 truncate rounded-md bg-background/80 px-3 py-1 text-[11px] text-muted-foreground">
          {path}
        </div>
      </div>
      <div className="bg-background p-4">{children}</div>
    </div>
  );
}
