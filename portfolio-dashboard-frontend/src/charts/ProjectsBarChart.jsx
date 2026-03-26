import React, { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";

function ProjectsBarChart() {
  const [data, setData] = useState([]);
  const [error, setError] = useState(null);

  const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:1907/pdbapp/api";

  useEffect(() => {
    fetch(`${API_BASE_URL}/projects/stats`)
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch project stats");
        return res.json();
      })
      .then((stats) => {
        // Convert { "2023": 4, "2024": 3 } → [{ year: "2023", count: 4 }, ...]
        const formatted = Object.entries(stats).map(([year, count]) => ({
          year,
          count,
        }));
        setData(formatted);
      })
      .catch((err) => {
        console.error(err);
        setError("Could not load project statistics");
      });
  }, []);

  return (
    <div className="p-4 rounded-xl shadow bg-white">
      <h2 className="text-lg font-semibold mb-4">Projects per Year</h2>

      {error ? (
        <p className="text-red-500 text-sm">{error}</p>
      ) : data.length === 0 ? (
        <p className="text-gray-500 text-sm">No project data available</p>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="year" />
            <YAxis allowDecimals={false} />
            <Tooltip />
            <Legend />
            <Bar
              dataKey="count"
              fill="#3b82f6"
              radius={[5, 5, 0, 0]}
              name="Projects"
            />
          </BarChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

export default ProjectsBarChart;
