import { useState, useEffect, useRef, useCallback } from "react";
import ProjectsTable from "../components/ProjectsTable";
import ProjectModal from "../components/ProjectModal";
import ConfirmDialog from "../components/ConfirmDialog";
import { fetchProjects, deleteProject } from "../api/projectService";
import { toast } from "react-toastify";

function Projects() {
  const [projects, setProjects] = useState([]);
  const [editing, setEditing] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState(null);

  const [offline, setOffline] = useState(false); // 🆕
  const cacheKey = "cache:projects";
  const hasFetched = useRef(false);

  const loadProjects = useCallback(async (showToast = true) => {
    try {
      setLoading(true);
      setOffline(false);
      const data = await fetchProjects();
      setProjects(data);
      localStorage.setItem(cacheKey, JSON.stringify({ ts: Date.now(), data }));
      if (showToast) toast.success(`Loaded ${data.length} project(s)`);
      return true;
    } catch (error) {
      console.error(error);
      const cached = localStorage.getItem(cacheKey);
      if (cached) {
        const { data } = JSON.parse(cached);
        setProjects(data);
        setOffline(true);
        toast.warn("Backend unreachable — showing cached data");
      } else {
        toast.error("Failed to load projects");
        setProjects([]);
      }
      return false;
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    if (hasFetched.current) return;
    hasFetched.current = true;
    loadProjects();
  }, [loadProjects]);

  const handleRefresh = async () => {
    if (refreshing) return;
    setRefreshing(true);
    const success = await loadProjects(false);
    if (success) toast.info("Projects list refreshed", { autoClose: 2000 });
    setRefreshing(false);
  };

  function handleEdit(project) {
    if (offline) return toast.warn("Can't edit while offline");
    setEditing(project);
    setModalOpen(true);
  }

  function handleDeleteRequest(id) {
    if (offline) return toast.warn("Can't delete while offline");
    const project = projects.find((p) => p.id === id);
    if (!project) return;
    setProjectToDelete(project);
    setConfirmOpen(true);
  }

  async function confirmDelete() {
    try {
      await deleteProject(projectToDelete.id);
      toast.success("Project deleted successfully", { autoClose: 1500 });
      await loadProjects(false);
    } catch (error) {
      console.error(error);
      toast.error(error.message || "Failed to delete project", {
        autoClose: 3000,
      });
    } finally {
      setConfirmOpen(false);
      setProjectToDelete(null);
    }
  }

  function cancelDelete() {
    setConfirmOpen(false);
    setProjectToDelete(null);
  }

  async function handleSaveSuccess() {
    await loadProjects(false);
  }

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-gray-600">
        <svg
          className="animate-spin h-8 w-8 text-blue-500 mb-3"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          ></circle>
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8v8H4z"
          ></path>
        </svg>
        <p>Loading projects...</p>
      </div>
    );
  }

  return (
    <div>
      {offline && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-amber-800 text-sm">
          Offline mode — showing last cached data. Edits disabled.
        </div>
      )}

      <div className="flex items-center justify-between mb-4">
        <h2 className="text-2xl font-semibold">Projects</h2>
        <div className="flex gap-2">
          <button
            onClick={handleRefresh}
            disabled={refreshing}
            className={`px-4 py-2 rounded border ${
              refreshing
                ? "bg-gray-300 text-gray-700 cursor-not-allowed"
                : "bg-blue-600 text-white hover:bg-blue-700"
            }`}
          >
            {refreshing ? "Refreshing..." : "↻ Refresh"}
          </button>

          <button
            onClick={() => {
              if (offline) return toast.warn("Can't add project while offline");
              setEditing(null);
              setModalOpen(true);
            }}
            className={`px-4 py-2 rounded text-white ${
              offline
                ? "bg-gray-300 cursor-not-allowed"
                : "bg-green-600 hover:bg-green-700"
            }`}
          >
            + New Project
          </button>
        </div>
      </div>

      <ProjectsTable
        data={projects}
        onEdit={handleEdit}
        onDelete={handleDeleteRequest}
      />

      <ProjectModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSave={handleSaveSuccess}
        project={editing}
      />

      <ConfirmDialog
        open={confirmOpen}
        title="Confirm Deletion"
        message={`Are you sure you want to delete “${
          projectToDelete?.title || "this project"
        }”? This action cannot be undone.`}
        onConfirm={confirmDelete}
        onCancel={cancelDelete}
      />
    </div>
  );
}

export default Projects;
