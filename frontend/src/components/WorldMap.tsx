import { motion } from "framer-motion";

// Major airports with approximate x,y positions on a 1200x600 map
const airports = [
  { id: "JFK", x: 310, y: 195, label: "New York" },
  { id: "LAX", x: 145, y: 220, label: "Los Angeles" },
  { id: "ORD", x: 260, y: 185, label: "Chicago" },
  { id: "LHR", x: 530, y: 155, label: "London" },
  { id: "CDG", x: 545, y: 165, label: "Paris" },
  { id: "FRA", x: 560, y: 160, label: "Frankfurt" },
  { id: "DXB", x: 680, y: 240, label: "Dubai" },
  { id: "SIN", x: 810, y: 330, label: "Singapore" },
  { id: "NRT", x: 890, y: 200, label: "Tokyo" },
  { id: "SYD", x: 905, y: 430, label: "Sydney" },
  { id: "GRU", x: 370, y: 390, label: "São Paulo" },
  { id: "JNB", x: 610, y: 410, label: "Johannesburg" },
  { id: "DEL", x: 740, y: 230, label: "Delhi" },
  { id: "ICN", x: 865, y: 200, label: "Seoul" },
  { id: "MIA", x: 280, y: 260, label: "Miami" },
  { id: "YYZ", x: 285, y: 170, label: "Toronto" },
  { id: "MEX", x: 215, y: 275, label: "Mexico City" },
  { id: "IST", x: 610, y: 185, label: "Istanbul" },
  { id: "BKK", x: 800, y: 290, label: "Bangkok" },
  { id: "HKG", x: 840, y: 255, label: "Hong Kong" },
];

const routes: [string, string][] = [
  ["JFK", "LHR"], ["JFK", "CDG"], ["LAX", "NRT"], ["LAX", "SYD"],
  ["ORD", "FRA"], ["ORD", "LHR"], ["LHR", "DXB"], ["CDG", "DXB"],
  ["DXB", "SIN"], ["DXB", "DEL"], ["SIN", "SYD"], ["SIN", "NRT"],
  ["NRT", "ICN"], ["GRU", "LHR"], ["GRU", "JNB"], ["JNB", "DXB"],
  ["DEL", "BKK"], ["BKK", "HKG"], ["HKG", "NRT"], ["MIA", "GRU"],
  ["YYZ", "LHR"], ["MEX", "MIA"], ["IST", "DXB"], ["FRA", "IST"],
  ["LAX", "ORD"], ["JFK", "MIA"], ["CDG", "FRA"], ["DEL", "SIN"],
  ["ICN", "HKG"], ["SYD", "NRT"],
];

const getAirport = (id: string) => airports.find((a) => a.id === id)!;

const WorldMap = () => {
  return (
    <div className="relative w-full h-full flex items-center justify-center overflow-hidden">
      {/* Background grid */}
      <div className="absolute inset-0 opacity-[0.03]"
        style={{
          backgroundImage: `radial-gradient(circle, hsl(var(--primary)) 1px, transparent 1px)`,
          backgroundSize: "40px 40px",
        }}
      />

      <svg
        viewBox="0 0 1000 520"
        className="w-full max-w-[1400px] h-auto"
        preserveAspectRatio="xMidYMid meet"
      >
        <defs>
          <radialGradient id="airportGlow" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stopColor="hsl(199, 89%, 70%)" stopOpacity="0.8" />
            <stop offset="100%" stopColor="hsl(199, 89%, 70%)" stopOpacity="0" />
          </radialGradient>
          <filter id="glow">
            <feGaussianBlur stdDeviation="2" result="blur" />
            <feMerge>
              <feMergeNode in="blur" />
              <feMergeNode in="SourceGraphic" />
            </feMerge>
          </filter>
          <linearGradient id="routeGradient1" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor="hsl(217, 91%, 60%)" stopOpacity="0.15" />
            <stop offset="50%" stopColor="hsl(199, 89%, 60%)" stopOpacity="0.5" />
            <stop offset="100%" stopColor="hsl(217, 91%, 60%)" stopOpacity="0.15" />
          </linearGradient>
          <linearGradient id="routeGradient2" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor="hsl(42, 92%, 56%)" stopOpacity="0.1" />
            <stop offset="50%" stopColor="hsl(42, 92%, 56%)" stopOpacity="0.35" />
            <stop offset="100%" stopColor="hsl(42, 92%, 56%)" stopOpacity="0.1" />
          </linearGradient>
        </defs>

        {/* Routes */}
        {routes.map(([fromId, toId], i) => {
          const from = getAirport(fromId);
          const to = getAirport(toId);
          const midX = (from.x + to.x) / 2;
          const midY = (from.y + to.y) / 2 - Math.abs(from.x - to.x) * 0.12;
          const gradient = i % 3 === 0 ? "url(#routeGradient2)" : "url(#routeGradient1)";

          return (
            <motion.path
              key={`${fromId}-${toId}`}
              d={`M ${from.x} ${from.y} Q ${midX} ${midY} ${to.x} ${to.y}`}
              fill="none"
              stroke={gradient}
              strokeWidth={i % 3 === 0 ? 1.5 : 1}
              initial={{ pathLength: 0, opacity: 0 }}
              animate={{ pathLength: 1, opacity: 1 }}
              transition={{ duration: 1.5, delay: 0.3 + i * 0.04, ease: "easeOut" }}
            />
          );
        })}

        {/* Airport dots */}
        {airports.map((airport, i) => (
          <g key={airport.id}>
            {/* Outer glow */}
            <motion.circle
              cx={airport.x}
              cy={airport.y}
              r={10}
              fill="url(#airportGlow)"
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.8 + i * 0.05, duration: 0.4 }}
            />
            {/* Pulse ring */}
            <motion.circle
              cx={airport.x}
              cy={airport.y}
              r={4}
              fill="none"
              stroke="hsl(199, 89%, 70%)"
              strokeWidth={0.5}
              initial={{ opacity: 0 }}
              animate={{ opacity: [0.4, 0, 0.4], r: [4, 12, 4] }}
              transition={{ delay: 1.5 + i * 0.1, duration: 3, repeat: Infinity, ease: "easeInOut" }}
            />
            {/* Core dot */}
            <motion.circle
              cx={airport.x}
              cy={airport.y}
              r={3}
              fill="hsl(199, 89%, 70%)"
              filter="url(#glow)"
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.8 + i * 0.05, duration: 0.3 }}
            />
            {/* Label */}
            <motion.text
              x={airport.x}
              y={airport.y - 12}
              textAnchor="middle"
              fill="hsl(210, 40%, 75%)"
              fontSize="8"
              fontFamily="Inter, sans-serif"
              fontWeight="500"
              initial={{ opacity: 0 }}
              animate={{ opacity: 0.7 }}
              transition={{ delay: 1.2 + i * 0.05, duration: 0.4 }}
            >
              {airport.label}
            </motion.text>
          </g>
        ))}
      </svg>
    </div>
  );
};

export default WorldMap;
