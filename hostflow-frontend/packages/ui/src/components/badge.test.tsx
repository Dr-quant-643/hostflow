import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Badge } from "./badge";

describe("Badge", () => {
  it("renders label", () => {
    render(<Badge>PUBLISHED</Badge>);
    expect(screen.getByText("PUBLISHED")).toBeInTheDocument();
  });

  it("applies variant class", () => {
    render(<Badge variant="success">Paid</Badge>);
    expect(screen.getByText("Paid").className).toContain("text-success");
  });
});
