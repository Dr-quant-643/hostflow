import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { PropertyCard } from "./property-card";

describe("PropertyCard", () => {
  it("renders core property info", () => {
    render(
      <PropertyCard
        imageUrl="/villa.jpg"
        name="Modern Nairobi Villa"
        location="Nairobi"
        pricePerNight="KSh 8,500"
        rating={4.9}
        status="PUBLISHED"
      />,
    );
    expect(screen.getByText("Modern Nairobi Villa")).toBeInTheDocument();
    expect(screen.getByText("Nairobi")).toBeInTheDocument();
    expect(screen.getByText("PUBLISHED")).toBeInTheDocument();
  });

  it("fires onToggleFavorite", () => {
    const onToggle = vi.fn();
    render(
      <PropertyCard
        imageUrl="/villa.jpg"
        name="Modern Nairobi Villa"
        location="Nairobi"
        pricePerNight="KSh 8,500"
        onToggleFavorite={onToggle}
      />,
    );
    fireEvent.click(screen.getByRole("button", { name: /favorites/ }));
    expect(onToggle).toHaveBeenCalledOnce();
  });
});
