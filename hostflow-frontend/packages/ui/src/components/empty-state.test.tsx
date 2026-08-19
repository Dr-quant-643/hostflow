import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { EmptyState } from "./empty-state";
import { Button } from "./button";

describe("EmptyState", () => {
  it("requires and renders title, description, and optional action", () => {
    render(
      <EmptyState
        title="No properties yet"
        description="You haven't added any properties to this organization."
        action={<Button>Add property</Button>}
      />,
    );
    expect(screen.getByText("No properties yet")).toBeInTheDocument();
    expect(
      screen.getByText(/haven't added any properties/),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Add property" }),
    ).toBeInTheDocument();
  });
});
