import { useState, useEffect } from "react";
import { Database, ArrowLeft, Trash2, RefreshCw, Table as TableIcon } from "lucide-react";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8081";

const AdminPanel = ({ onBack }: { onBack: () => void }) => {
  const [tables, setTables] = useState<string[]>([]);
  const [activeTable, setActiveTable] = useState("");
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetch(`${API_URL}/api/tables`)
      .then((res) => res.json())
      .then((list) => {
        setTables(list);
        if (list.length > 0) setActiveTable(list[0]);
      })
      .catch((err) => console.error("Error:", err));
  }, []);

  const loadTableData = (tableName: string) => {
    setLoading(true);
    fetch(`${API_URL}/api/data?table=${tableName}`)
      .then((res) => res.json())
      .then((json) => {
        setData(json);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error:", err);
        setLoading(false);
      });
  };

  useEffect(() => {
    if (activeTable) loadTableData(activeTable);
  }, [activeTable]);

  return (
    <div className="min-h-screen bg-[#0a0a0a] text-white p-6 font-sans">
      <div className="flex justify-between items-center mb-8 bg-white/5 p-4 rounded-2xl border border-white/10 shadow-xl">
        <div className="flex items-center gap-4">
          <button 
            onClick={onBack}
            className="p-2 hover:bg-white/10 rounded-full transition-colors text-blue-400"
          >
            <ArrowLeft size={24} />
          </button>
          <div>
            <h1 className="text-xl font-bold flex items-center gap-2">
              <Database className="text-blue-500" size={20} /> SQL Server Admin
            </h1>
            <p className="text-[10px] text-gray-500 uppercase tracking-widest">Database Management System</p>
          </div>
        </div>
        
        <button 
          onClick={() => loadTableData(activeTable)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600/20 text-blue-400 rounded-lg hover:bg-blue-600/30 transition-all border border-blue-600/30 text-xs font-semibold"
        >
          <RefreshCw size={14} className={loading ? "animate-spin" : ""} /> Update
        </button>
      </div>

      <div className="flex flex-col md:flex-row gap-6">
        <div className="w-full md:w-64 space-y-2">
          <h3 className="text-[10px] font-bold text-gray-500 uppercase px-2 mb-4">Tables</h3>
          {tables.map((table) => (
            <button
              key={table}
              onClick={() => setActiveTable(table)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm transition-all border ${
                activeTable === table
                  ? "bg-blue-600 border-blue-500 text-white shadow-lg shadow-blue-900/20"
                  : "bg-white/5 border-transparent text-gray-400 hover:bg-white/10 hover:text-gray-200"
              }`}
            >
              <TableIcon size={16} opacity={0.6} />
              {table}
            </button>
          ))}
        </div>

        <div className="flex-1 bg-white/5 border border-white/10 rounded-2xl overflow-hidden shadow-2xl backdrop-blur-sm">
          <div className="overflow-x-auto max-h-[70vh]">
            <table className="w-full text-left border-collapse">
              <thead className="sticky top-0 bg-[#1a1a1a] z-10 shadow-md">
                <tr>
                  {data.length > 0 && Object.keys(data[0]).map((key) => (
                    <th key={key} className="px-6 py-4 text-[11px] font-bold uppercase text-gray-500 border-b border-white/10">
                      {key}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {data.map((row, i) => (
                  <tr key={i} className="group hover:bg-white/[0.03] transition-colors">
                    {Object.values(row).map((val: any, j) => (
                      <td key={j} className="px-6 py-4 text-sm text-gray-300 font-mono">
                        {val === "" ? <span className="text-gray-600 italic">empty</span> : String(val)}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          
          {data.length === 0 && !loading && (
            <div className="flex flex-col items-center justify-center p-20 text-gray-500 italic">
              <Database size={48} className="mb-4 opacity-10" />
              Table "{activeTable}" empty
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;
