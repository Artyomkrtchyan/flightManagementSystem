import { Globe, Plane, Activity } from "lucide-react";

interface StatusBarProps {
  airportsCount: number;
  routesCount: number;
}

const StatusBar = ({ airportsCount, routesCount }: StatusBarProps) => {
  return (
    <div className="flex justify-center pb-6">
      <div className="flex items-center gap-8 bg-slate-900/80 backdrop-blur-xl border border-white/10 px-8 py-3 rounded-2xl shadow-2xl transition-all hover:border-cyan-500/30">
        
        {/* Airports Count */}
        <div className="flex items-center gap-3">
          <Globe className="text-blue-400" size={18} />
          <span className="text-slate-400 text-sm font-medium uppercase tracking-wider">Airports</span>
          <span className="text-white text-xl font-bold font-mono">{airportsCount}</span>
        </div>

        {/* Divider */}
        <div className="h-6 w-[1px] bg-white/10" />

        {/* Routes Count */}
        <div className="flex items-center gap-3">
          <Plane className="text-blue-400 -rotate-45" size={18} />
          <span className="text-slate-400 text-sm font-medium uppercase tracking-wider">Routes</span>
          <span className="text-white text-xl font-bold font-mono">{routesCount}</span>
        </div>

        {/* Divider */}
        <div className="h-6 w-[1px] bg-white/10" />

        {/* Status */}
        <div className="flex items-center gap-3">
          <Activity className="text-emerald-400 animate-pulse" size={18} />
          <span className="text-slate-400 text-sm font-medium uppercase tracking-wider">Status</span>
          <span className="text-emerald-400 text-xl font-bold font-mono uppercase">Live</span>
        </div>
        
      </div>
    </div>
  );
};

export default StatusBar; 
