import { motion } from "framer-motion";
import { Route, MapPin, Network, Scissors, TreePine, Wallet } from "lucide-react";

const algorithms = [
  { label: "Dijkstra (Fastest)", icon: Route },
  { label: "Dijkstra (Cheapest)", icon: Wallet },
  { label: "BFS (K Connections)", icon: Network },
  { label: "Articulation Points", icon: Scissors },
  { label: "Min Spanning Tree", icon: TreePine },
  { label: "Travel Budget", icon: MapPin },
];

const AlgorithmNav = () => {
  return (
    <nav className="w-full bg-navy-medium/80 backdrop-blur-xl border-b border-border nav-shadow px-6 py-3">
      <div className="max-w-[1600px] mx-auto flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-primary/20 flex items-center justify-center">
            <Network className="w-4 h-4 text-primary" />
          </div>
          <h1 className="text-lg font-semibold tracking-tight">
            Flight Network <span className="text-primary">Analyzer</span>
          </h1>
        </div>

        <div className="flex items-center gap-2">
          {algorithms.map((algo, i) => {
            const Icon = algo.icon;
            return (
              <motion.button
                key={algo.label}
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.06, duration: 0.3 }}
                className="flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium
                  bg-secondary/60 text-muted-foreground border border-border
                  hover:bg-primary/15 hover:text-primary hover:border-primary/30
                  transition-all duration-200 cursor-pointer"
              >
                <Icon className="w-3.5 h-3.5" />
                <span className="hidden xl:inline">{algo.label}</span>
              </motion.button>
            );
          })}
        </div>
      </div>
    </nav>
  );
};

export default AlgorithmNav;
