"use client";

import { motion } from "framer-motion";

// A vintage push-pin world map -- doubles as the visual metaphor for
// "searching across places and pinning what you like" that the rest of the
// hero (location markers, saved-property hearts) plays off of.
const MAP_IMAGE =
  "https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&w=2400&q=80";

const PINS = [
  { label: "Nairobi", top: "26%", left: "62%", delay: 0 },
  { label: "Mombasa", top: "58%", left: "70%", delay: 0.4 },
  { label: "Diani", top: "70%", left: "65%", delay: 0.8 },
  { label: "Naivasha", top: "36%", left: "47%", delay: 1.2 },
  { label: "Kisumu", top: "44%", left: "32%", delay: 1.6 },
];

const FLOATING_PHOTOS = [
  {
    src: "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=500&q=70",
    caption: "🌲 Cabin retreats",
    top: "13%",
    left: "6%",
    rotate: -7,
  },
  {
    src: "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=500&q=70",
    caption: "🏊 Villas with pools",
    top: "11%",
    left: "80%",
    rotate: 6,
  },
  {
    src: "https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=500&q=70",
    caption: "🌴 City escapes",
    top: "73%",
    left: "8%",
    rotate: 5,
  },
  {
    src: "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=500&q=70",
    caption: "🏡 Family homes",
    top: "75%",
    left: "82%",
    rotate: -6,
  },
];

export function HeroMapBackdrop() {
  return (
    <div className="pointer-events-none absolute inset-0 overflow-hidden">
      <motion.div
        initial={{ scale: 1.08, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 1.4, ease: "easeOut" }}
        className="absolute inset-0"
      >
        <img src={MAP_IMAGE} alt="" className="h-full w-full object-cover" />
        <div className="absolute inset-0 bg-gradient-to-t from-[#140c26]/95 via-[#241640]/75 to-[#2c1b4d]/45" />
        <div className="absolute inset-0 bg-gradient-to-br from-sapphire-500/20 via-transparent to-purple-500/25" />
      </motion.div>

      <motion.div
        animate={{ opacity: [0.25, 0.4, 0.25] }}
        transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
        className="absolute -left-24 top-10 h-72 w-72 rounded-full bg-purple-500/30 blur-[100px]"
      />
      <motion.div
        animate={{ opacity: [0.2, 0.35, 0.2] }}
        transition={{ duration: 7, repeat: Infinity, ease: "easeInOut", delay: 1 }}
        className="absolute -right-16 bottom-0 h-80 w-80 rounded-full bg-sapphire-400/25 blur-[110px]"
      />

      {PINS.map((pin) => (
        <div key={pin.label} className="absolute" style={{ top: pin.top, left: pin.left }}>
          <motion.div
            initial={{ scale: 0.6, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ delay: pin.delay, duration: 0.5 }}
            className="relative flex flex-col items-center"
          >
            <span className="absolute -top-1 h-3 w-3 rounded-full bg-purple-300 shadow-[0_0_12px_4px_rgba(192,132,252,0.6)]" />
            <motion.span
              animate={{ scale: [1, 2.2], opacity: [0.6, 0] }}
              transition={{ duration: 2.2, repeat: Infinity, delay: pin.delay, ease: "easeOut" }}
              className="absolute -top-1 h-3 w-3 rounded-full bg-purple-300"
            />
            <span className="mt-2.5 whitespace-nowrap rounded-full bg-black/40 px-2 py-0.5 text-[10px] font-medium text-white/80 backdrop-blur-sm">
              {pin.label}
            </span>
          </motion.div>
        </div>
      ))}

      {FLOATING_PHOTOS.map((photo, i) => (
        <motion.div
          key={photo.caption}
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: [0, -10, 0] }}
          transition={{
            opacity: { delay: 0.6 + i * 0.15, duration: 0.6 },
            y: { duration: 5 + i, repeat: Infinity, ease: "easeInOut", delay: i * 0.4 },
          }}
          className="absolute hidden sm:block"
          style={{ top: photo.top, left: photo.left, rotate: `${photo.rotate}deg` }}
        >
          <div className="w-32 rounded-xl border-2 border-white/80 bg-white p-1.5 shadow-2xl sm:w-36">
            <img src={photo.src} alt="" className="h-20 w-full rounded-md object-cover sm:h-24" />
            <p className="mt-1 truncate text-center text-[10px] font-medium text-gray-700">
              {photo.caption}
            </p>
          </div>
        </motion.div>
      ))}
    </div>
  );
}
