import React, { useEffect, useState } from "react";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";

const COLORS = [
  "#3b82f6",
  "#10b981",
  "#f59e0b",
  "#8b5cf6",
  "#ef4444",
  "#6366f1",
];

function SkillsPieChart() {
  const [data, setData] = useState([]);
  const [error, setError] = useState(null);

  const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:1907/pdbapp/api";

  useEffect(() => {
    fetch(`${API_BASE_URL}/skills/stats`)
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch skill stats");
        return res.json();
      })
      .then((stats) => {
        // Convert { "Java": 90, "React": 80 } → [{ name: "Java", value: 90 }, ...]
        const formatted = Object.entries(stats).map(([name, proficiency]) => ({
          name,
          value: proficiency,
        }));
        setData(formatted);
      })
      .catch((err) => {
        console.error(err);
        setError("Could not load skill statistics");
      });
  }, []);

  return (
    <div className="p-4 rounded-xl shadow bg-white">
      <h2 className="text-lg font-semibold mb-4">Skills Distribution</h2>

      {error ? (
        <p className="text-red-500 text-sm">{error}</p>
      ) : data.length === 0 ? (
        <p className="text-gray-500 text-sm">No skill data available</p>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={data}
              dataKey="value"
              nameKey="name"
              cx="50%"
              cy="50%"
              outerRadius={100}
              label={({ name, value }) => `${name} (${value}%)`}
            >
              {data.map((entry, index) => (
                <Cell key={index} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
            <Legend iconType="circle" />
          </PieChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

export default SkillsPieChart;
