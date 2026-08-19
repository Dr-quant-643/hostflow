import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Card, CardHeader, CardTitle, CardContent } from "./card";

describe("Card", () => {
  it("renders title and content", () => {
    render(
      <Card>
        <CardHeader>
          <CardTitle>Property Overview</CardTitle>
        </CardHeader>
        <CardContent>3 active bookings</CardContent>
      </Card>,
    );
    expect(screen.getByText("Property Overview")).toBeInTheDocument();
    expect(screen.getByText("3 active bookings")).toBeInTheDocument();
  });
});
