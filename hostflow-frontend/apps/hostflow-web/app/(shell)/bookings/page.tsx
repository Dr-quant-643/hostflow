import { PageHeader } from "@hostflow/ui";
import { BookingList } from "@/components/bookings/booking-list";

export default function BookingsPage() {
  return (
    <div>
      <PageHeader
        title="Bookings"
        description="All reservations across your properties — bookings are created by guests, staff can confirm or cancel"
      />
      <BookingList />
    </div>
  );
}
