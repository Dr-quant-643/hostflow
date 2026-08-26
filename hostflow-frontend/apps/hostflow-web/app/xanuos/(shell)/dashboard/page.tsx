"use client";

import type { ComponentProps, ReactNode } from "react";
import Link from "next/link";
import { Stack, Grid, StatCard, PageHeader } from "@hostflow/ui";
import {
  usePropertyOccupancy,
  useMonthlyRevenue,
  useGuestSegments,
} from "@hostflow/api-client/src/hooks/use-analytics";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import {
  useRentalTenants,
  useLeasePendingCount,
  useLeaseStatusCount,
  useOwnerRentalInquiries,
} from "@hostflow/api-client/src/hooks/use-rental";
import { useBookings, useBookingPendingCount } from "@hostflow/api-client/src/hooks/use-bookings";
import { useOwnerWorkOrders, useOpenWorkOrderCount } from "@hostflow/api-client/src/hooks/use-maintenance";
import { useExpenseTotalByCategory } from "@hostflow/api-client/src/hooks/use-billing";
import { useContacts } from "@hostflow/api-client/src/hooks/use-crm";
import { useCampaigns } from "@hostflow/api-client/src/hooks/use-marketing";
import { useInvoices } from "@hostflow/api-client/src/hooks/use-billing";
import { useMyOrgUsers } from "@hostflow/api-client/src/hooks/use-team";
import { useMeetingRoomCount, useVisitorOnSiteCount } from "@hostflow/api-client/src/hooks/use-office";
import { useRetailOccupancySummary, useUpcomingMallEventCount } from "@hostflow/api-client/src/hooks/use-mall";
import { OccupancyChart } from "@/components/xanuos/dashboard/occupancy-chart";
import { formatKES } from "@/lib/currency";

// Every stat is a link to the XanuOS tab that "expounds more" on it -- the
// dashboard is a jumping-off point, not a dead end. Numbers carry color
// (via StatCard's tone prop); labels stay muted -- the "bold figures, gray
// words" split the user asked for, same idea as a stock ticker.
function LinkedStat({ href, ...statProps }: { href: string } & ComponentProps<typeof StatCard>) {
  return (
    <Link href={href} className="block transition-transform hover:-translate-y-0.5">
      <StatCard {...statProps} />
    </Link>
  );
}

function SectionHeading({ children }: { children: ReactNode }) {
  return <h2 className="text-sm font-semibold uppercase tracking-wide text-muted-foreground">{children}</h2>;
}

export default function DashboardPage() {
  const { data: occupancy } = usePropertyOccupancy();
  const { data: revenue } = useMonthlyRevenue();
  const { data: properties } = useProperties(100);
  const { data: tenants } = useRentalTenants(100);
  const { data: bookings } = useBookings(100);
  const { data: bookingPending } = useBookingPendingCount();
  const { data: leasePending } = useLeasePendingCount();
  const { data: activeLeases } = useLeaseStatusCount("ACTIVE");
  const { data: workOrders } = useOwnerWorkOrders(100);
  const { data: openWorkOrders } = useOpenWorkOrderCount();
  const { data: maintenanceCost } = useExpenseTotalByCategory("MAINTENANCE");
  const { data: inquiries } = useOwnerRentalInquiries();
  const { data: contacts } = useContacts(undefined, 100);
  const { data: campaigns } = useCampaigns(100);
  const { data: invoices } = useInvoices(100);
  const { data: team } = useMyOrgUsers(100);
  const { data: segments } = useGuestSegments();
  const { data: meetingRooms } = useMeetingRoomCount();
  const { data: visitorsOnSite } = useVisitorOnSiteCount();
  const { data: retailOccupancy } = useRetailOccupancySummary();
  const { data: upcomingMallEvents } = useUpcomingMallEventCount();

  const activeProperties = properties?.filter((p) => p.status === "ACTIVE").length;
  const totalBookings = occupancy?.reduce((sum, p) => sum + p.totalBookings, 0);
  const latestMonthRevenue = revenue?.[0]?.paidTotal;
  const pendingApprovals = (bookingPending ?? 0) + (leasePending ?? 0);
  const answeredInquiries = inquiries?.filter((i) => i.status === "REPLIED").length ?? 0;
  const unansweredInquiries = inquiries?.filter((i) => i.status === "OPEN").length ?? 0;
  const publishedCampaigns = campaigns?.filter((c) => c.status === "PUBLISHED").length;
  const outstandingInvoices = invoices?.filter((i) => i.status === "ISSUED" || i.status === "OVERDUE");
  const outstandingTotal = outstandingInvoices?.reduce((sum, i) => sum + Number(i.amount ?? 0), 0);
  const vipCount = segments?.filter((s) => s.segment === "VIP").length;
  const atRiskCount = segments?.filter((s) => s.segment === "AT_RISK").length;

  // Office/Mall tiles only matter to owners who actually have that property
  // type -- most owners here are residential/hotel-only, so this section
  // stays hidden for them instead of showing permanently-empty tiles.
  const hasOfficeProperty = properties?.some((p) => p.propertyType === "OFFICE" || p.propertyType === "MIXED_USE");
  const hasMallProperty = properties?.some((p) => p.propertyType === "RETAIL_MALL" || p.propertyType === "MIXED_USE");

  return (
    <Stack gap="xl">
      <PageHeader title="Dashboard" description="Everything across NazilCo and XanuOS, at a glance" />

      <Stack gap="sm">
        <SectionHeading>Portfolio</SectionHeading>
        <Grid cols={4} gap="md">
          <LinkedStat
            href="/xanuos/properties"
            label="Active Properties"
            value={activeProperties !== undefined ? String(activeProperties) : "—"}
            tone="info"
          />
          <LinkedStat
            href="/xanuos/rental/tenants"
            label="Tenants"
            value={tenants ? String(tenants.length) : "—"}
            tone="info"
          />
          <LinkedStat
            href="/xanuos/bookings"
            label="Pending Approvals"
            value={String(pendingApprovals)}
            tone={pendingApprovals > 0 ? "warning" : "default"}
          />
          <LinkedStat
            href="/xanuos/billing"
            label="Revenue (Latest Month)"
            value={latestMonthRevenue ? formatKES(latestMonthRevenue) : "—"}
            tone="success"
          />
        </Grid>
      </Stack>

      <Stack gap="sm">
        <SectionHeading>Bookings &amp; Reservations</SectionHeading>
        <Grid cols={4} gap="md">
          <LinkedStat
            href="/xanuos/bookings"
            label="Total Bookings"
            value={totalBookings !== undefined ? String(totalBookings) : bookings ? String(bookings.length) : "—"}
          />
          <LinkedStat
            href="/xanuos/rental"
            label="Active Leases"
            value={activeLeases !== undefined ? String(activeLeases) : "—"}
            tone="success"
          />
          <LinkedStat
            href="/xanuos/analytics/customers"
            label="VIP Guests"
            value={vipCount !== undefined ? String(vipCount) : "—"}
            tone="info"
          />
          <LinkedStat
            href="/xanuos/analytics/customers"
            label="At-Risk Guests"
            value={atRiskCount !== undefined ? String(atRiskCount) : "—"}
            tone={atRiskCount ? "warning" : "default"}
          />
        </Grid>
      </Stack>

      <Stack gap="sm">
        <SectionHeading>Operations</SectionHeading>
        <Grid cols={4} gap="md">
          <LinkedStat
            href="/xanuos/maintenance"
            label="Maintenance Issues"
            value={workOrders ? String(workOrders.length) : "—"}
            tone={openWorkOrders ? "warning" : "default"}
          />
          <LinkedStat
            href="/xanuos/expenses"
            label="Maintenance Cost"
            value={maintenanceCost ? formatKES(maintenanceCost) : "—"}
            tone="destructive"
          />
          <LinkedStat
            href="/xanuos/notifications"
            label="Inquiries Answered"
            value={String(answeredInquiries)}
            tone="success"
          />
          <LinkedStat
            href="/xanuos/notifications"
            label="Inquiries Unanswered"
            value={String(unansweredInquiries)}
            tone={unansweredInquiries > 0 ? "warning" : "default"}
          />
        </Grid>
      </Stack>

      <Stack gap="sm">
        <SectionHeading>Growth &amp; Finance</SectionHeading>
        <Grid cols={4} gap="md">
          <LinkedStat
            href="/xanuos/crm"
            label="CRM Contacts"
            value={contacts ? String(contacts.length) : "—"}
          />
          <LinkedStat
            href="/xanuos/marketing"
            label="Active Campaigns"
            value={publishedCampaigns !== undefined ? String(publishedCampaigns) : "—"}
          />
          <LinkedStat
            href="/xanuos/billing"
            label="Outstanding Invoices"
            value={outstandingTotal !== undefined ? formatKES(outstandingTotal) : "—"}
            tone={outstandingTotal ? "warning" : "default"}
          />
          <LinkedStat
            href="/xanuos/team"
            label="Team Members"
            value={team ? String(team.content.length) : "—"}
          />
        </Grid>
      </Stack>

      {(hasOfficeProperty || hasMallProperty) && (
        <Stack gap="sm">
          <SectionHeading>Office &amp; Mall</SectionHeading>
          <Grid cols={4} gap="md">
            {hasOfficeProperty && (
              <>
                <LinkedStat
                  href="/xanuos/office"
                  label="Meeting Rooms"
                  value={meetingRooms !== undefined ? String(meetingRooms) : "—"}
                />
                <LinkedStat
                  href="/xanuos/office"
                  label="Visitors On Site"
                  value={visitorsOnSite !== undefined ? String(visitorsOnSite) : "—"}
                  tone="info"
                />
              </>
            )}
            {hasMallProperty && (
              <>
                <LinkedStat
                  href="/xanuos/mall"
                  label="Retail Occupancy"
                  value={retailOccupancy ? `${retailOccupancy.occupied}/${retailOccupancy.total}` : "—"}
                  tone="success"
                />
                <LinkedStat
                  href="/xanuos/mall"
                  label="Upcoming Mall Events"
                  value={upcomingMallEvents !== undefined ? String(upcomingMallEvents) : "—"}
                />
              </>
            )}
          </Grid>
        </Stack>
      )}

      <OccupancyChart />
    </Stack>
  );
}
