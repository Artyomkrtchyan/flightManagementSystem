import { Fragment, useState, useEffect } from "react";
import { MapContainer, TileLayer, CircleMarker, Polyline, Tooltip, Polygon } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { Wallet, Network, TreePine, Sun, Moon, Lamp, Database, Trash2, Search } from "lucide-react";
import StatusBar from "@/components/StatusBar";

import type { LatLngBoundsExpression } from "leaflet";
interface Airport {
  airportID: number;
  code: string;
  name?: string;
  lat: number;
  lng: number;
}

interface Route {
  source: number;
  destination: number;
  distance: number;
}

type Category = "DijkstraFastest" | "DijkstraCheapest" | "BFSK" | "CriticalAirports" | "MST" | "TravelBudget";

const Index = () => {
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [airports, setAirports] = useState<Airport[]>([]);
  const [routes, setRoutes] = useState<Route[]>([]);
  const [allRoutes, setAllRoutes] = useState<Route[]>([]);
  const [sourceAirport, setSourceAirport] = useState<Airport | null>(null);
  const [targetAirport, setTargetAirport] = useState<Airport | null>(null);
  const [highlightedPath, setHighlightedPath] = useState<number[]>([]);
  const [totalDistance, setTotalDistance] = useState<number | null>(null); 
  const [totalPrice, setTotalPrice] = useState<number | null>(null);
  const [isAdminOpen, setIsAdminOpen] = useState(false);
  const [maxBudget, setMaxBudget] = useState<number>(500);
  const [budgetRoutes, setBudgetRoutes] = useState<Route[]>([]);
  const [kValue, setKValue] = useState<number>(1);

const maxBounds: LatLngBoundsExpression = [
  [-85, -180],
  [85, 180]
];
  

const getArrowHead = (from: Airport, to: Airport) => {
  const dx = to.lng - from.lng;
  const dy = to.lat - from.lat;

  const angle = Math.atan2(dy, dx);

  const size = 1;

  const centerLat = from.lat + dy * 0.85;
  const centerLng = from.lng + dx * 0.85;

  const tip: [number, number] = [
    centerLat,
    centerLng
  ];

  const left: [number, number] = [
    centerLat - size * Math.sin(angle - Math.PI / 6),
    centerLng - size * Math.cos(angle - Math.PI / 6)
  ];

  const right: [number, number] = [
    centerLat - size * Math.sin(angle + Math.PI / 6),
    centerLng - size * Math.cos(angle + Math.PI / 6)
  ];

  return [tip, left, right];
};


const AdminPanel = ({ onBack }: { onBack: () => void }) => {
  const [data, setData] = useState<any[]>([]);
  const [tables, setTables] = useState<string[]>([]);
  const [activeTable, setActiveTable] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [newRowData, setNewRowData] = useState<any>({});

  useEffect(() => {
    fetch("http://localhost:8081/api/tables")
      .then(res => res.json())
      .then(json => {
        setTables(json);
        if (json.length > 0) setActiveTable(json[0]);
      });
  }, []);

  const loadTableData = (tableName: string) => {
    fetch(`http://localhost:8081/api/data?table=${tableName}`)
      .then(res => res.json())
      .then(json => setData(json));
  };

  useEffect(() => {
    if (activeTable) loadTableData(activeTable);
  }, [activeTable]);

  const handleDelete = async (row: any) => {

  const idKey = Object.keys(row).find(key => 
    key.toLowerCase() === `${activeTable.slice(0, -1).toLowerCase()}id` || 
    key.toLowerCase() === 'id'
  );

  if (!idKey) {
    alert("Can't find ID");
    return;
  }

  if (!window.confirm(`Are you sure you want to delete ID ${row[idKey]}?`)) return;

  try {
    const response = await fetch(
      `http://localhost:8081/api/delete?table=${activeTable}&col=${idKey}&id=${row[idKey]}`,
      { method: "DELETE" }
    );
    
    const result = await response.json();
    if (result.success) {
      loadTableData(activeTable);
    } else {
      alert("Error");
    }
  } catch (err) {
    console.error("Delete error:", err);
    alert("Connection Error");
  }
};

  const handleSave = async () => {
    try {
      const response = await fetch("http://localhost:8081/api/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          table: activeTable,
          data: newRowData
        })
      });

      const result = await response.json();
      if (result.success) {
        setIsAddModalOpen(false); 
        setNewRowData({});        
        loadTableData(activeTable); 
      } else {
        alert("Save Error");
      }
    } catch (err) {
      console.error("Save error:", err);
    }
  };

  const filteredData = data.filter(item =>
    Object.values(item).some(val => String(val).toLowerCase().includes(searchQuery.toLowerCase()))
  );

const toggleSelectAll = () => {
  if (selectedIds.length === filteredData.length) {
    setSelectedIds([]); 
  } else {
    const idKey = Object.keys(data[0] || {}).find(k => k.toLowerCase().includes("id"));
    if (idKey) {
      setSelectedIds(filteredData.map(item => item[idKey])); // Выбрать все ID
    }
  }
};

const toggleSelectRow = (id: number) => {
  setSelectedIds(prev => 
    prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
  );
};

const deleteSelected = async () => {
  if (selectedIds.length === 0) return;
  if (!window.confirm(`Delete (${selectedIds.length} rows)?`)) return;

  const idKey = Object.keys(data[0] || {}).find(k => k.toLowerCase().includes("id"));
  

  try {
    const promises = selectedIds.map(id => 
      fetch(`http://localhost:8081/api/delete?table=${activeTable}&col=${idKey}&id=${id}`, { method: "DELETE" })
    );
    await Promise.all(promises);
    setSelectedIds([]);
    loadTableData(activeTable);
  } catch (err) {
    console.error("Error", err);
  }
};


return (
    <div className="min-h-screen bg-[#0a0a0a] text-white p-8 overflow-auto">

      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl font-bold text-blue-500">Database Management</h1>
        <button onClick={onBack} className="bg-white/10 px-4 py-2 rounded-lg">Back to the map</button>
      </div>

      <div className="flex flex-wrap gap-2 mb-6">
        {tables.map(tab => (
          <button 
            key={tab} 
            onClick={() => {
              setActiveTable(tab);
              setSelectedIds([]); 
            }}
            className={`px-4 py-2 rounded ${activeTable === tab ? 'bg-blue-600' : 'bg-white/5'}`}
          >
            {tab}
          </button>
        ))}
      </div>

      <div className="mb-6 flex gap-4 items-center">
        <div className="relative">
          <input 
            type="text" 
            placeholder="Search..." 
            className="bg-white/5 border border-white/10 rounded-lg px-4 py-2 w-80 outline-none focus:border-blue-500/50 transition-all"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>


        <button 
          onClick={deleteSelected}
          disabled={selectedIds.length === 0}
          className={`px-6 py-2 rounded-lg font-bold transition-all flex items-center gap-2 ${
            selectedIds.length > 0 
            ? 'bg-red-600/20 text-red-500 border border-red-600/50 hover:bg-red-600 hover:text-white' 
            : 'bg-gray-800/50 text-gray-600 border border-transparent cursor-not-allowed'
          }`}
        >
          <Trash2 size={14} />
          Delete Selected ({selectedIds.length})
        </button>

        <button 
          onClick={() => {
            const fields: any = {};
            if (data.length > 0) {
              Object.keys(data[0]).forEach(key => {
                const isIdentity = key.toLowerCase() === `${activeTable.slice(0, -1).toLowerCase()}id` 
                                   || key.toLowerCase() === 'id';
                if (!isIdentity) fields[key] = ""; 
              });
            }
            setNewRowData(fields);
            setIsAddModalOpen(true);
          }}
          className="bg-green-600 hover:bg-green-500 px-6 py-2 rounded-lg font-bold ml-auto transition-all"
        >
          + Add row
        </button>
      </div>
      
      <div className="bg-white/5 border border-white/10 rounded-xl overflow-hidden shadow-2xl">
        <table className="w-full text-left">
          <thead className="bg-white/10 text-xs uppercase text-gray-400">
            <tr>
              <th className="px-6 py-4 w-10">
                <input 
                  type="checkbox" 
                  className="accent-blue-500"
                  onChange={toggleSelectAll}
                  checked={filteredData.length > 0 && selectedIds.length === filteredData.length}
                />
              </th>
              {data[0] && Object.keys(data[0]).map(key => <th key={key} className="px-6 py-4">{key}</th>)}
              <th className="px-6 py-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredData.map((item, idx) => {
              const idKey = Object.keys(item).find(k => k.toLowerCase().includes("id"));
              const rowId = item[idKey!];
              const isSelected = selectedIds.includes(rowId);

              return (
                <tr key={idx} className={`border-t border-white/5 hover:bg-white/[0.02] transition-colors ${isSelected ? 'bg-blue-500/10' : ''}`}>
                  <td className="px-6 py-4">
                    <input 
                      type="checkbox" 
                      className="accent-blue-500"
                      checked={isSelected}
                      onChange={() => toggleSelectRow(rowId)}
                    />
                  </td>
                  {Object.values(item).map((val, i) => (
                    <td key={i} className="px-6 py-4 text-sm text-gray-300">
                      {val === null ? <span className="text-gray-600 italic text-xs">null</span> : String(val)}
                    </td>
                  ))}
                  <td className="px-6 py-4 text-right">
                    <button 
                      onClick={() => handleDelete(item)} 
                      className="inline-flex items-center justify-center p-2 text-red-500 hover:bg-red-500/10 rounded-md transition-all"
                      title="Delete row"
                    >
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {isAddModalOpen && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-[2000]">
          <div className="bg-[#1a1a1a] p-8 rounded-2xl border border-white/10 w-full max-w-md shadow-2xl">
            <h2 className="text-xl font-bold mb-4 text-blue-400">Add in {activeTable}</h2>
            <div className="space-y-4 max-h-[60vh] overflow-y-auto pr-2 custom-scrollbar">
              {Object.keys(newRowData).map(key => (
                <div key={key}>
                  <label className="block text-[10px] uppercase font-bold text-gray-500 mb-1 ml-1">{key}</label>
                  <input 
                    className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2 outline-none focus:border-blue-500 transition-all text-sm"
                    value={newRowData[key]}
                    onChange={(e) => setNewRowData({...newRowData, [key]: e.target.value})}
                  />
                </div>
              ))}
            </div>
            <div className="flex gap-4 mt-8">
              <button 
                onClick={() => setIsAddModalOpen(false)} 
                className="flex-1 bg-white/5 hover:bg-white/10 py-2 rounded-xl transition-all font-semibold"
              >
                Cancel
              </button>
              <button 
                onClick={handleSave} 
                className="flex-1 bg-blue-600 hover:bg-blue-500 py-2 rounded-xl font-bold shadow-lg shadow-blue-600/20 transition-all"
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
  

  useEffect(() => {
  const fetchData = async () => {
    try {
      const response = await fetch("http://localhost:8081/graph");
      const data = await response.json();
      setAirports(data.airports || []);
      setRoutes(data.routes || []);
      setAllRoutes(data.routes || []); 
    } catch (error) {
      console.error("Connection error:", error);
    }
  };
  fetchData();
}, []);

  const findPath = async (fromId: number, toId: number) => {
    const apiType = selectedCategory === "DijkstraCheapest" ? "cheapest" : "fastest";
    try {
      const response = await fetch(`http://localhost:8081/${apiType}?id=${fromId}&to=${toId}`);
      if (!response.ok) throw new Error("Server error");
      const data = await response.json();
      if (data.path && data.path.length > 0) {
        setHighlightedPath(data.path);
        setTotalPrice(data.totalPrice ?? null);
        setTotalDistance(data.totalDistance ?? null);
      } else {
        setHighlightedPath([]);
      }
    } catch (error) {
      console.error("Fetch error:", error);
    }
  };

const [bfsRoutes, setBfsRoutes] = useState<Route[]>([]); // Добавьте новый стейт

const findReachable = async (fromId: number) => {
  try {
    const response = await fetch(`http://localhost:8081/bfs?id=${fromId}&k=${Math.max(1, kValue)}`);
    const data = await response.json();

    if (data.reachableIds) {
      setHighlightedPath(data.reachableIds.map((id: any) => Number(id)));
      setBfsRoutes((data.usedRoutes || []).map((r: any) => ({
        source: Number(r.source),
        destination: Number(r.destination),
        distance: Number(r.distance)
      }))); 
    } else {
      setHighlightedPath([]);
      setBfsRoutes([]);
    }
  } catch (error) {
    console.error("BFS error:", error);
  }
};

const findCritical = async () => {
  try {
    const response = await fetch("http://localhost:8081/critical");
    if (!response.ok) throw new Error("Server error");
    const data = await response.json();
    
    if (data.criticalIds) {
      setHighlightedPath(data.criticalIds);  
    }
  } catch (error) {
    console.error("Critical Airports error:", error);
  }
};

const findMST = async () => {
  try {
    const response = await fetch("http://localhost:8081/mst");
    if (!response.ok) throw new Error("Server error");
    const data = await response.json();
    
    if (data.mstRoutes) {
     
      setRoutes(data.mstRoutes); 
      setHighlightedPath([1]); 
    }
  } catch (error) {
    console.error("MST error:", error);
  }
};

const findByBudget = async (airportId: number) => {
  setHighlightedPath([]);

  try {
    const response = await fetch(
      `http://localhost:8081/budget?id=${airportId}&maxBudget=${maxBudget}`
    );

    const data = await response.json();

    if (data.reachableIds) {
      const cleaned = data.reachableIds
        .map((id: any) => Number(id))
        .filter((id: number) => id !== airportId); // 🔥 убираем старт

      setHighlightedPath(cleaned);
    }

  } catch (error) {
    console.error("Budget Error:", error);
  }
};

 const handleAirportClick = (airport: Airport) => {

  if (selectedCategory === "BFSK") {
    setSourceAirport(airport);
    findReachable(airport.airportID); 
    return;
  }

  if (selectedCategory === "TravelBudget") {
    setSourceAirport(airport);
    findByBudget(airport.airportID);
    return;
  }

    if (selectedCategory !== "DijkstraFastest" && selectedCategory !== "DijkstraCheapest") return;
    if (!sourceAirport || (sourceAirport && targetAirport)) {
      setSourceAirport(airport);
      setTargetAirport(null);
      setHighlightedPath([]);
      setTotalDistance(null);
      setTotalPrice(null);
    } else if (sourceAirport && !targetAirport) {
      if (sourceAirport.airportID === airport.airportID) return;
      setTargetAirport(airport);
      findPath(sourceAirport.airportID, airport.airportID);
    }
  };

  const getAirportById = (id: number) => airports.find((a) => a.airportID === id);

useEffect(() => {
  if (selectedCategory === "BFSK" && sourceAirport) {
    findReachable(sourceAirport.airportID);
  }
}, [kValue, sourceAirport, selectedCategory]);

if (isAdminOpen) {
    return <AdminPanel onBack={() => setIsAdminOpen(false)} />;
  }

  return (
    <div className="relative h-screen w-screen bg-[#0a0a0a] text-white overflow-hidden">
      
      {/* Navigation */}
      <div className="absolute top-4 left-1/2 -translate-x-1/2 z-[1000] flex gap-2 bg-black/80 backdrop-blur-md rounded-xl px-4 py-2 border border-white/10 shadow-2xl">
        {[
          { label: "Fastest", category: "DijkstraFastest", icon: Lamp },
          { label: "Cheapest", category: "DijkstraCheapest", icon: Moon },
          { label: "BFS for K", category: "BFSK", icon: Network },
          { label: "Critical", category: "CriticalAirports", icon: Sun },
          { label: "MST", category: "MST", icon: TreePine },
          {label: "Budget", category: "TravelBudget", icon: Wallet },
        ].map((algo) => {
          const Icon = algo.icon;
          return (
            <button
              key={algo.category}
onClick={() => {
  const newCategory = algo.category as Category;
  setSelectedCategory(newCategory);
  setSourceAirport(null);
  setTargetAirport(null);
  setHighlightedPath([]);
  setBfsRoutes([]);     

  if (newCategory === "MST") {
    findMST();
  } else {

    setRoutes(allRoutes); 
    if (newCategory === "CriticalAirports") {
      findCritical();
    }
  }
}}
              className={`flex items-center gap-2 px-3 py-2 rounded-lg text-xs font-semibold transition-all duration-300
              ${selectedCategory === algo.category 
                ? "bg-blue-600 text-white shadow-[0_0_15px_rgba(37,99,235,0.4)]" 
                : "bg-white/5 text-gray-400 hover:bg-white/10"}`}
            >
              <Icon size={14} />
              <span className="hidden md:inline">{algo.label}</span>
            </button>
          );
        })}
        <div className="w-[1px] bg-white/10 mx-1" /> 
<button 
  onClick={() => setIsAdminOpen(true)}
  className="flex items-center gap-2 px-3 py-2 rounded-lg text-xs font-semibold bg-amber-600/20 text-amber-500 hover:bg-amber-600/30 border border-amber-600/30 transition-all"
>
  <Database size={14} />
  <span className="hidden md:inline">DB</span>
</button>
      </div>

      {selectedCategory === "BFSK" && (
        <div className="absolute top-20 left-1/2 -translate-x-1/2 z-[1000] flex items-center gap-3 bg-black/80 border border-amber-500/40 p-2 px-4 rounded-full backdrop-blur-md animate-in fade-in slide-in-from-top-4">
          <span className="text-[10px] font-bold text-amber-400 uppercase tracking-tighter">Max Transfers (K):</span>
          <input 
            type="number" 
            min="1" 
            max="10"
            value={kValue}
            onChange={(e) => setKValue(Number(e.target.value))}
            className="bg-transparent border-b border-amber-500 w-8 text-center text-sm outline-none font-mono"
          />
        </div>
      )}

{selectedCategory === "TravelBudget" && (
  <div className="absolute top-20 left-1/2 -translate-x-1/2 z-[1000] flex items-center gap-3 bg-black/80 border border-green-500/40 p-2 px-4 rounded-full backdrop-blur-md">

    <span className="text-[10px] font-bold text-green-500 uppercase tracking-tighter">
      Max Budget ($):
    </span>

    <input
      type="number"
      value={maxBudget}
      onChange={(e) => setMaxBudget(Number(e.target.value))}
      className="bg-transparent border-b border-green-500 w-20 text-center text-sm outline-none font-mono text-white"
    />

  </div>
)}



      <MapContainer
        center={[20, 0]} zoom={3} minZoom={3}
        maxBounds={maxBounds} maxBoundsViscosity={1.0}
        style={{ height: "100%", width: "100%", background: "#0a0a0a" }}
        zoomControl={false}
      >
        <TileLayer url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png" attribution='&copy;  CARTO' noWrap={true} />

{routes.map((route, idx) => {
  
  const from = getAirportById(route.source);
  const to = getAirportById(route.destination);
  if (!from || !to) return null;


  const isBFSMode = selectedCategory === "BFSK";
  const isMSTMode = selectedCategory === "MST";
  const isCriticalMode = selectedCategory === "CriticalAirports";
  const isBudgetMode = selectedCategory === "TravelBudget";
  
  let isHighlighted = false;

  if (isMSTMode) {
    isHighlighted = true;
  } else if (isBFSMode) {
    isHighlighted = bfsRoutes.some(
      r => Number(r.source) === Number(route.source) && Number(r.destination) === Number(route.destination)
    );
  }   else if (isBudgetMode) {
    isHighlighted = budgetRoutes.some(
      r => Number(r.source) === Number(route.source) && Number(r.destination) === Number(route.destination)
    );
  } else if (!isCriticalMode) {
const sIdx = highlightedPath.indexOf(route.source);
const dIdx = highlightedPath.indexOf(route.destination);

isHighlighted = (
  sIdx !== -1 &&
  dIdx !== -1 &&
  dIdx === sIdx + 1   
);
  }

const shouldFilter = 
  (highlightedPath.length > 0 && !isCriticalMode && !isMSTMode && !isBFSMode) ||
  (isBudgetMode && budgetRoutes.length > 0);

if (isBudgetMode) {
  const exists = budgetRoutes.some(
    r =>
      (r.source === route.source && r.destination === route.destination) ||
      (r.source === route.destination && r.destination === route.source)
  );

  if (!exists) return null;
} else if (shouldFilter && !isHighlighted) return null;

  return (
    <Fragment key={`route-group-${idx}`}>
      <Polyline
        positions={[[from.lat, from.lng], [to.lat, to.lng]]}
        pathOptions={{
          color: "#fbbf24", 
          weight: isHighlighted ? 2.5 : 0.8,
          opacity: isHighlighted ? 0.8 : 0.2,
          dashArray: isHighlighted && !isMSTMode ? "" : (isMSTMode ? "" : "5, 10"),
        }}
      />
      
      {isHighlighted && (
  <Polygon
    positions={getArrowHead(from, to)}
    pathOptions={{
      color: "#fbbf24",
      fillColor: "#fbbf24",
      fillOpacity: 1,
      weight: 1
    }}
  >
    <Tooltip direction="top" opacity={0.9}>
      <div className="text-[9px] font-bold">
        {from.code} → {to.code}
      </div>
    </Tooltip>
  </Polygon>
)}
    </Fragment>
  );
})}

        {airports.map((airport) => {
          const isSelected = sourceAirport?.airportID === airport.airportID || targetAirport?.airportID === airport.airportID;
          const isInPath = highlightedPath.includes(airport.airportID);
          
          return (
            <CircleMarker
              key={airport.airportID}
              center={[airport.lat, airport.lng]}
              radius={isSelected ? 6 : 4}
              eventHandlers={{ click: () => handleAirportClick(airport) }}
              pathOptions={{ 
                color: isSelected ? "#fff" : (isInPath ? "#fbbf24" : "#3b82f6"), 
                fillColor: isSelected ? "#3b82f6" : (isInPath ? "#fbbf24" : "#1e40af"), 
                fillOpacity: 1,
                weight: isSelected ? 2 : 1 
              }}
            >
              <Tooltip direction="top" offset={[0, -5]} opacity={1}>
                <div className="bg-slate-900 text-white p-2 rounded border border-blue-500 text-[10px]">
                  <span className="font-bold text-blue-400">{airport.code}</span> - {airport.name}
                </div>
              </Tooltip>
            </CircleMarker>
          );
        })}
      </MapContainer>

      {selectedCategory === "BFSK" && sourceAirport && (
        <div className="absolute right-6 top-24 z-[1001] w-64 bg-black/90 backdrop-blur-xl border border-amber-500/30 rounded-2xl p-5 shadow-[0_0_30px_rgba(251,191,36,0.15)]">
          <h3 className="text-amber-400 font-bold text-xs uppercase tracking-widest mb-2 flex items-center gap-2">
            <Network size={14} /> Reachability
          </h3>
          <p className="text-[10px] text-gray-400 mb-4">From: <span className="text-white">{sourceAirport.code}</span></p>
          
          <div className="text-center p-4 bg-white/5 rounded-xl border border-white/5">
            <div className="text-3xl font-mono text-amber-400">{highlightedPath.length}</div>
            <div className="text-[9px] text-gray-500 uppercase">Airports Reachable</div>
          </div>
          
          <button 
  onClick={() => { 
    setHighlightedPath([]); 
    setSourceAirport(null); 
    setBfsRoutes([]);
  }}
  className="mt-4 w-full py-2 bg-white/5 hover:bg-white/10 border border-white/10 rounded-lg text-[10px] transition-all"
>
  Reset BFS
</button>
        </div>
      )}

{selectedCategory === "TravelBudget" && (
  <div className="absolute right-6 top-4 z-[1001] w-64 bg-black/90 backdrop-blur-xl border border-green-500/30 rounded-2xl p-5 shadow-[0_0_30px_rgba(34,197,94,0.15)]">

    {/* HEADER */}
    <h3 className="text-green-400 font-bold text-xs uppercase tracking-widest mb-3 flex items-center gap-2">
      <Wallet size={14} /> Budget Reachability
    </h3>

    {/* COUNTER */}
    <div className="text-center p-4 bg-white/5 rounded-xl border border-white/5 mb-4">
      <div className="text-3xl font-mono text-green-400">
        {highlightedPath.length}
      </div>
      <div className="text-[9px] text-gray-500 uppercase">
        Airports Available
      </div>
    </div>

    {/* LIST */}
    <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
      {highlightedPath.map(id => {
        const airport = getAirportById(id);
        return (
          <div key={id} className="text-xs text-gray-300 flex items-center gap-2">
            ✈ {airport?.code} - {airport?.name}
          </div>
        );
      })}
    </div>

  </div>
)}

      {highlightedPath.length > 0 && (selectedCategory === "DijkstraFastest" || selectedCategory === "DijkstraCheapest") && (
        <div className="absolute right-6 top-24 z-[1001] w-72 bg-black/90 backdrop-blur-xl border border-blue-500/30 rounded-2xl p-5 shadow-[0_0_30px_rgba(59,130,246,0.2)]">
          <h3 className="text-amber-400 font-bold text-xs uppercase tracking-widest mb-4 flex items-center gap-2">
            <div className="w-1.5 h-1.5 bg-amber-400 rounded-full" />
            Route Details
          </h3>
          <div className="max-h-[250px] overflow-y-auto pr-2 space-y-3 custom-scrollbar">
            {highlightedPath.map((id, index) => {
              const airport = getAirportById(id);
              return (
                <div key={`p-${id}-${index}`} className="flex items-center gap-3">
                  <div className={`w-5 h-5 rounded-full border flex items-center justify-center text-[9px] 
                    ${index === 0 ? "bg-blue-600 border-blue-600" : "border-gray-600 text-gray-400"}`}>
                    {index + 1}
                  </div>
                  <span className="text-xs font-mono text-gray-200">{airport?.code}</span>
                </div>
              );
            })}
          </div>
          <div className="mt-6 pt-4 border-t border-white/10">
            <p className="text-[10px] text-gray-500 uppercase mb-1">Total {selectedCategory === "DijkstraCheapest" ? "Price" : "Distance"}</p>
            <p className="text-xl font-mono text-amber-400">
              {selectedCategory === "DijkstraCheapest" ? `$${totalPrice?.toFixed(2)}` : `${totalDistance?.toFixed(0)} KM`}
            </p>
          </div>
        </div>
      )}

      <div className="absolute bottom-0 w-full z-[1000]">
        <StatusBar airportsCount={airports.length} routesCount={routes.length} />
      </div>
    </div>
  );
};

export default Index;
