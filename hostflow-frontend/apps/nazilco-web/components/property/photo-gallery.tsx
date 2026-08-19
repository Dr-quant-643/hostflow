"use client";

import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { X, ChevronLeft, ChevronRight, Grid2x2 } from "lucide-react";

export function PhotoGallery({ photoUrls }: { photoUrls: string[] }) {
  const [lightboxIndex, setLightboxIndex] = useState<number | null>(null);

  if (photoUrls.length === 0) {
    return <div className="h-[420px] w-full animate-pulse rounded-2xl bg-muted" />;
  }

  const main = photoUrls[0];
  const rest = photoUrls.slice(1, 5);

  return (
    <>
      <div className="grid grid-cols-4 grid-rows-2 gap-2 overflow-hidden rounded-2xl" style={{ height: 420 }}>
        <button
          type="button"
          onClick={() => setLightboxIndex(0)}
          className="group relative col-span-2 row-span-2 overflow-hidden"
        >
          <img
            src={main}
            alt="Property photo 1"
            className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
          />
        </button>
        {rest.map((url, i) => (
          <button
            key={url}
            type="button"
            onClick={() => setLightboxIndex(i + 1)}
            className="group relative col-span-1 row-span-1 overflow-hidden"
          >
            <img
              src={url}
              alt={`Property photo ${i + 2}`}
              className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
            />
          </button>
        ))}
        {Array.from({ length: Math.max(0, 4 - rest.length) }).map((_, i) => (
          <div key={`empty-${i}`} className="col-span-1 row-span-1 bg-muted" />
        ))}
      </div>

      {photoUrls.length > 1 && (
        <button
          type="button"
          onClick={() => setLightboxIndex(0)}
          className="mt-2 flex items-center gap-1.5 rounded-full border border-border px-3 py-1.5 text-xs font-medium hover:bg-muted"
        >
          <Grid2x2 className="h-3.5 w-3.5" />
          Show all {photoUrls.length} photos
        </button>
      )}

      <AnimatePresence>
        {lightboxIndex !== null && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 p-4"
            onClick={() => setLightboxIndex(null)}
          >
            <button
              type="button"
              onClick={() => setLightboxIndex(null)}
              className="absolute right-5 top-5 rounded-full bg-white/10 p-2 text-white hover:bg-white/20"
              aria-label="Close"
            >
              <X className="h-5 w-5" />
            </button>

            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                setLightboxIndex((i) => (i === null ? 0 : (i - 1 + photoUrls.length) % photoUrls.length));
              }}
              className="absolute left-4 rounded-full bg-white/10 p-2 text-white hover:bg-white/20"
              aria-label="Previous photo"
            >
              <ChevronLeft className="h-6 w-6" />
            </button>

            <motion.img
              key={lightboxIndex}
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.96 }}
              transition={{ duration: 0.25 }}
              src={photoUrls[lightboxIndex]}
              alt={`Property photo ${lightboxIndex + 1}`}
              className="max-h-[85vh] max-w-[90vw] rounded-lg object-contain"
              onClick={(e) => e.stopPropagation()}
            />

            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                setLightboxIndex((i) => (i === null ? 0 : (i + 1) % photoUrls.length));
              }}
              className="absolute right-4 rounded-full bg-white/10 p-2 text-white hover:bg-white/20"
              aria-label="Next photo"
            >
              <ChevronRight className="h-6 w-6" />
            </button>

            <div className="absolute bottom-5 rounded-full bg-white/10 px-3 py-1 text-xs text-white">
              {lightboxIndex + 1} / {photoUrls.length}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
