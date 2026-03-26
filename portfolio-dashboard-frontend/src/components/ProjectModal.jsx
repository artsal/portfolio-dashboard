import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { createProject, updateProject } from "../api/projectService";

export default function ProjectModal({ open, onClose, onSave, project }) {
  const [form, setForm] = useState({
    id: null,
    title: "",
    description: "",
    techStack: "",
    githubLink: "",
    status: "Planned",
    startDate: "",
    endDate: "",
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (project) {
      setForm({
        id: project.id,
        title: project.title || "",
        description: project.description || "",
        techStack: Array.isArray(project.techStack)
          ? project.techStack.join(", ")
          : project.techStack || "",
        githubLink: project.githubLink || "",
        status: project.status || "Planned",
        startDate: project.startDate || "",
        endDate: project.endDate || "",
      });
    } else {
      setForm({
        id: null,
        title: "",
        description: "",
        techStack: "",
        githubLink: "",
        status: "Planned",
        startDate: "",
        endDate: "",
      });
    }
  }, [project, open]);

  function handleChange(e) {
    setForm((s) => ({ ...s, [e.target.name]: e.target.value }));
  }

  async function handleSave() {
    if (!form.title.trim()) {
      toast.warn("Project title is required");
      return;
    }

    const payload = {
      ...form,
      techStack: form.techStack
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean)
        .join(","),
    };

    try {
      setLoading(true);
      if (form.id) {
        await updateProject(payload.id, payload);
        toast.success("Project updated successfully!", { autoClose: 1500 });
      } else {
        await createProject(payload);
        toast.success("Project created successfully!", { autoClose: 1500 });
      }

      onSave && (await onSave()); // auto-refresh parent list
      onClose && onClose();
    } catch (err) {
      console.error(err);
      toast.error(err.message || "Failed to save project", { autoClose: 4000 });
    } finally {
      setLoading(false);
    }
  }

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl p-6 animate-fadeIn">
        <h3 className="text-lg font-semibold mb-4">
          {form.id ? "Edit Project" : "Add Project"}
        </h3>

        <div className="grid grid-cols-1 gap-3">
          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            placeholder="Project title"
            className="border px-3 py-2 rounded"
            disabled={loading}
          />
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            placeholder="Short description"
            className="border px-3 py-2 rounded"
            disabled={loading}
          />
          <input
            name="techStack"
            value={form.techStack}
            onChange={handleChange}
            placeholder="Tech stack (comma separated)"
            className="border px-3 py-2 rounded"
            disabled={loading}
          />
          <input
            name="githubLink"
            value={form.githubLink}
            onChange={handleChange}
            placeholder="GitHub repository link (optional)"
            className="border px-3 py-2 rounded"
            disabled={loading}
          />
          <select
            name="status"
            value={form.status}
            onChange={handleChange}
            className="border px-3 py-2 rounded"
            disabled={loading}
          >
            <option>Planned</option>
            <option>Active</option>
            <option>Completed</option>
          </select>
          <div className="flex gap-2">
            <input
              name="startDate"
              type="date"
              value={form.startDate}
              onChange={handleChange}
              className="border px-3 py-2 rounded w-1/2"
              disabled={loading}
            />
            <input
              name="endDate"
              type="date"
              value={form.endDate}
              onChange={handleChange}
              className="border px-3 py-2 rounded w-1/2"
              disabled={loading}
            />
          </div>
        </div>

        <div className="flex justify-end gap-2 mt-5">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded border"
            disabled={loading}
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            disabled={loading}
            className={`px-4 py-2 rounded text-white ${
              loading ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"
            }`}
          >
            {loading ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
