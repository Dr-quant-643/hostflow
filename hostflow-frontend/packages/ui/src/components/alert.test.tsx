import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Alert } from "./alert";

describe("Alert", () => {
  it("renders title and children", () => {
    render(
      <Alert title="Publish failed">We couldn't publish this property.</Alert>,
    );
    expect(screen.getByRole("alert")).toBeInTheDocument();
    expect(screen.getByText("Publish failed")).toBeInTheDocument();
    expect(
      screen.getByText("We couldn't publish this property."),
    ).toBeInTheDocument();
  });

  it("applies destructive variant styling", () => {
    render(<Alert variant="destructive" title="Error" />);
    expect(screen.getByRole("alert").className).toContain(
      "border-destructive/30",
    );
  });
});
