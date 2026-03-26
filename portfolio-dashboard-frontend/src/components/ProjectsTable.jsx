import { useMemo, useState } from "react";
import { fetchProjectById } from "../api/projectService";
import { toast } from "react-toastify";
import ProjectViewModal from "./ProjectViewModal";

/**
 * Enhanced Projects Table
 * - Handles backend CSV `techStack`
 * - Displays GitHub link
 * - Adds End Date column + sort
 * - Includes View modal with loading state
 */

function sortBy(data, key, dir) {
  return [...data].sort((a, b) => {
    const av = a[key] ?? "";
    const bv = b[key] ?? "";
    if (av === bv) return 0;
    if (dir === "asc") return av > bv ? 1 : -1;
    return av < bv ? 1 : -1;
  });
}

export default function ProjectsTable({ data = [], onEdit, onDelete }) {
  const [q, setQ] = useState("");
  const [sortKey, setSortKey] = useState("title");
  const [sortDir, setSortDir] = useState("asc");
  const [page, setPage] = useState(1);
  const pageSize = 5;

  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const filtered = useMemo(() => {
    const lower = q.trim().toLowerCase();
    const filtered = data.filter((p) => {
      const techStacks = Array.isArray(p.techStack)
        ? p.techStack.join(" ")
        : p.techStack || "";

      return (
        p.title.toLowerCase().includes(lower) ||
        techStacks.toLowerCase().includes(lower) ||
        (p.status || "").toLowerCase().includes(lower) ||
        (p.startDate || "").toLowerCase().includes(lower) ||
        (p.endDate || "").toLowerCase().includes(lower)
      );
    });

    return sortBy(filtered, sortKey, sortDir);
  }, [data, q, sortKey, sortDir]);

  const total = filtered.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const pageData = filtered.slice((page - 1) * pageSize, page * pageSize);

  function toggleSort(key) {
    if (key === sortKey) {
      setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDir("asc");
    }
    setPage(1);
  }

  async function handleViewProject(id) {
    try {
      setSelectedProject(null);
      setIsLoading(true);
      setViewModalOpen(true);

      const proj = await fetchProjectById(id);
      setSelectedProject(proj);
    } catch (err) {
      console.error(err);
      toast.error("Could not fetch project details", { autoClose: 4000 });
      setViewModalOpen(false);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div>
      {/* Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-4 gap-3">
        <div className="flex items-center gap-2 w-full sm:w-auto">
          <input
            value={q}
            onChange={(e) => {
              setQ(e.target.value);
              setPage(1);
            }}
            placeholder="Search projects, tech stack, status, or dates..."
            className="px-3 py-2 border rounded w-full sm:w-80 md:w-96"
          />
          <button
            onClick={() => setQ("")}
            className="px-3 py-2 bg-gray-100 rounded"
          >
            Clear
          </button>
        </div>

        <div className="flex items-center gap-2 text-sm text-gray-600">
          <span>Sort:</span>
          <button
            onClick={() => toggleSort("title")}
            className={`px-2 py-1 rounded ${
              sortKey === "title" ? "bg-blue-50" : "hover:bg-gray-100"
            }`}
          >
            Title {sortKey === "title" ? (sortDir === "asc" ? "↑" : "↓") : ""}
          </button>
          <button
            onClick={() => toggleSort("status")}
            className={`px-2 py-1 rounded ${
              sortKey === "status" ? "bg-blue-50" : "hover:bg-gray-100"
            }`}
          >
            Status {sortKey === "status" ? (sortDir === "asc" ? "↑" : "↓") : ""}
          </button>
          <button
            onClick={() => toggleSort("startDate")}
            className={`px-2 py-1 rounded ${
              sortKey === "startDate" ? "bg-blue-50" : "hover:bg-gray-100"
            }`}
          >
            Start{" "}
            {sortKey === "startDate" ? (sortDir === "asc" ? "↑" : "↓") : ""}
          </button>
          <button
            onClick={() => toggleSort("endDate")}
            className={`px-2 py-1 rounded ${
              sortKey === "endDate" ? "bg-blue-50" : "hover:bg-gray-100"
            }`}
          >
            End {sortKey === "endDate" ? (sortDir === "asc" ? "↑" : "↓") : ""}
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto bg-white rounded shadow">
        <table className="min-w-full divide-y">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left text-sm font-medium text-gray-700">
                Title
              </th>
              <th className="px-4 py-2 text-left text-sm font-medium text-gray-700 hidden md:table-cell">
                Tech Stack
              </th>
              <th className="px-4 py-2 text-left text-sm font-medium text-gray-700">
                Status
              </th>
              <th className="px-4 py-2 text-left text-sm font-medium text-gray-700">
                Start
              </th>
              <th className="px-4 py-2 text-left text-sm font-medium text-gray-700">
                End
              </th>
              <th className="px-4 py-2 text-right text-sm font-medium text-gray-700">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {pageData.map((p) => (
              <tr key={p.id}>
                <td className="px-4 py-3 text-sm">
                  <div className="font-medium text-gray-900">{p.title}</div>
                  <div className="text-gray-500 text-xs">{p.description}</div>
                  {p.githubLink && (
                    <a
                      href={p.githubLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-500 text-xs hover:underline"
                    >
                      View on GitHub
                    </a>
                  )}
                </td>
                <td className="px-4 py-3 text-sm hidden md:table-cell">
                  {(typeof p.techStack === "string"
                    ? p.techStack.split(",")
                    : p.techStack || []
                  )
                    .slice(0, 3)
                    .map((t, i) => (
                      <span
                        key={i}
                        className="inline-block mr-2 text-xs bg-gray-100 px-2 py-1 rounded"
                      >
                        {t.trim()}
                      </span>
                    ))}
                </td>
                <td className="px-4 py-3 text-sm">
                  <span
                    className={`px-2 py-1 rounded text-xs ${
                      p.status === "Active"
                        ? "bg-green-100 text-green-800"
                        : p.status === "Completed"
                        ? "bg-blue-100 text-blue-800"
                        : "bg-yellow-100 text-yellow-800"
                    }`}
                  >
                    {p.status}
                  </span>
                </td>
                <td className="px-4 py-3 text-sm">{p.startDate}</td>
                <td className="px-4 py-3 text-sm text-gray-500">
                  {p.endDate || "—"}
                </td>
                <td className="px-4 py-3 text-sm text-right">
                  <button
                    onClick={() => handleViewProject(p.id)}
                    className="text-sm px-3 py-1 mr-2 bg-blue-50 text-blue-600 rounded"
                  >
                    View
                  </button>
                  <button
                    onClick={() => onEdit && onEdit(p)}
                    className="text-sm px-3 py-1 mr-2 bg-indigo-50 text-indigo-600 rounded"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete && onDelete(p.id)}
                    className="text-sm px-3 py-1 bg-red-50 text-red-600 rounded"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}

            {pageData.length === 0 && (
              <tr>
                <td className="px-4 py-8 text-center text-gray-500" colSpan={6}>
                  No projects found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="flex items-center justify-between mt-3 text-sm text-gray-600">
        <div>{total} project(s)</div>
        <div className="flex items-center gap-2">
          <button
            className="px-2 py-1 rounded border"
            onClick={() => setPage((p) => Math.max(1, p - 1))}
            disabled={page === 1}
          >
            Prev
          </button>
          <div>
            Page {page} / {totalPages}
          </div>
          <button
            className="px-2 py-1 rounded border"
            onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}
          >
            Next
          </button>
        </div>
      </div>

      {/* View Modal */}
      <ProjectViewModal
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
        project={selectedProject}
        isLoading={isLoading}
      />
    </div>
  );
}
