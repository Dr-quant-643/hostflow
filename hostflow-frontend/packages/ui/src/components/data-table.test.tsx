import { describe, it, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { type ColumnDef } from "@tanstack/react-table";
import { DataTable } from "./data-table";

interface Row {
  id: string;
  name: string;
  status: string;
}

const columns: ColumnDef<Row>[] = [
  { accessorKey: "name", header: "Name" },
  { accessorKey: "status", header: "Status" },
];

const data: Row[] = [
  { id: "1", name: "Nairobi Villa", status: "PUBLISHED" },
  { id: "2", name: "Mombasa Loft", status: "DRAFT" },
];

describe("DataTable", () => {
  it("renders rows", () => {
    render(<DataTable columns={columns} data={data} />);
    expect(screen.getByText("Nairobi Villa")).toBeInTheDocument();
    expect(screen.getByText("Mombasa Loft")).toBeInTheDocument();
  });

  it("filters rows via search", () => {
    render(
      <DataTable
        columns={columns}
        data={data}
        searchKey="name"
        searchPlaceholder="Search properties"
      />,
    );
    const input = screen.getByPlaceholderText("Search properties");
    fireEvent.change(input, { target: { value: "Nairobi" } });
    expect(screen.getByText("Nairobi Villa")).toBeInTheDocument();
    expect(screen.queryByText("Mombasa Loft")).not.toBeInTheDocument();
  });

  it("shows empty state when no data", () => {
    render(<DataTable columns={columns} data={[]} />);
    expect(screen.getByText("No results.")).toBeInTheDocument();
  });
});
