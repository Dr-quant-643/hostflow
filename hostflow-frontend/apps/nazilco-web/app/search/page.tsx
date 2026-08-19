"use client";

import { motion } from "framer-motion";
import { Stack, PageHeader } from "@hostflow/ui";
import { BrandBackground } from "@/components/layout/brand-background";
import { SearchForm } from "@/components/search/search-form";
import { SearchResults } from "@/components/search/search-results";

export default function SearchPage() {
  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-6xl p-6">
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <PageHeader title="Search" description="Find a property for your trip" />
        </motion.div>
        <SearchForm />
        <SearchResults />
      </Stack>
    </>
  );
}
