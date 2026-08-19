import { PageHeader } from "@hostflow/ui";
import { AdminBookingList } from "@/components/bookings/admin-booking-list";

export default function AdminBookingsPage() {
  return (
    <div>
      <PageHeader
        title="Bookings Oversight"
        description="All guest bookings across NazilCo"
      />
      <AdminBookingList />
    </div>
  );
}
