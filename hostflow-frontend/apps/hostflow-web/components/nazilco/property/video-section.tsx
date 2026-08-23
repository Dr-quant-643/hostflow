"use client";

export function PropertyVideoSection({ videoUrls }: { videoUrls: string[] }) {
  if (videoUrls.length === 0) return null;

  return (
    <div className="border-b pb-6">
      <h2 className="mb-4 font-medium">Property tour</h2>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {videoUrls.map((url) => (
          // eslint-disable-next-line jsx-a11y/media-has-caption -- owner-uploaded property walkthroughs have no caption track
          <video
            key={url}
            src={url}
            controls
            preload="metadata"
            className="aspect-video w-full rounded-xl bg-muted object-cover"
          />
        ))}
      </div>
    </div>
  );
}
