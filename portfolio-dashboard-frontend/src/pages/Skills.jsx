import React, { useEffect, useState, useRef, useCallback } from "react";
import { motion } from "framer-motion";
import { toast } from "react-toastify";
import {
  fetchSkills,
  addSkill,
  updateSkill,
  deleteSkill,
} from "../api/skillService";
import SkillModal from "../components/SkillModal";
import ConfirmDialog from "../components/ConfirmDialog";

const tailwindToHexMap = {
  "bg-blue-500": "#3B82F6",
  "bg-green-500": "#10B981",
  "bg-purple-500": "#8B5CF6",
  "bg-orange-500": "#F97316",
  "bg-yellow-500": "#EAB308",
  "bg-pink-500": "#EC4899",
  "bg-teal-500": "#14B8A6",
  "bg-gray-500": "#6B7280",
};
const resolveColor = (color) =>
  !color ? null : tailwindToHexMap[color] ? tailwindToHexMap[color] : color;
const pastelPalette = [
  "#6EA8FE",
  "#A78BFA",
  "#FCA5A5",
  "#F9A8D4",
  "#FBBF24",
  "#34D399",
  "#38BDF8",
  "#F87171",
  "#C084FC",
];
function getColorForCategory(category) {
  if (!category) return "#94a3b8";
  let hash = 0;
  for (let i = 0; i < category.length; i++) {
    hash = category.charCodeAt(i) + ((hash << 5) - hash);
  }
  return pastelPalette[Math.abs(hash % pastelPalette.length)];
}

export default function Skills() {
  const [skills, setSkills] = useState([]);
  const [expanded, setExpanded] = useState({});
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [offline, setOffline] = useState(false);
  const cacheKey = "cache:skills";

  const [modalOpen, setModalOpen] = useState(false);
  const [editingSkill, setEditingSkill] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [skillToDelete, setSkillToDelete] = useState(null);
  const firstLoadDone = useRef(false);

  const loadSkills = useCallback(async (showToast = true) => {
    try {
      setLoading(true);
      setOffline(false);
      const data = await fetchSkills();
      setSkills(data || []);
      localStorage.setItem(cacheKey, JSON.stringify({ ts: Date.now(), data }));
      setExpanded((prev) => {
        if (Object.keys(prev).length > 0) return prev;
        const obj = {};
        (data || []).forEach((s) => (obj[s.category || "Other"] = true));
        return obj;
      });
      if (showToast) {
        toast.success(`Loaded ${data?.length || 0} skill(s)`, {
          autoClose: 1600,
        });
      }
      return true;
    } catch (err) {
      console.error("Failed to load skills", err);
      const cached = localStorage.getItem(cacheKey);
      if (cached) {
        const { data } = JSON.parse(cached);
        setSkills(data);
        setOffline(true);
        toast.warn("Backend unreachable — showing cached data");
      } else {
        toast.error("Failed to load skills", { autoClose: 3000 });
        setSkills([]);
      }
      return false;
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    // only run once on mount
    if (!firstLoadDone.current) {
      firstLoadDone.current = true;
      loadSkills(true); // show toast once
    }
  }, [loadSkills]);

  const handleRefresh = async () => {
    if (refreshing) return;
    setRefreshing(true);
    const success = await loadSkills(false);
    if (success) toast.info("Skills list refreshed", { autoClose: 2000 });
    setRefreshing(false);
  };

  const toggleCategory = (cat) => {
    setExpanded((prev) => ({ ...prev, [cat]: !prev[cat] }));
  };

  const openNewModal = () => {
    if (offline) return toast.warn("Can't add skills while offline");
    setEditingSkill(null);
    setModalOpen(true);
  };
  const openEditModal = (skill) => {
    if (offline) return toast.warn("Can't edit while offline");
    setEditingSkill(skill);
    setModalOpen(true);
  };

  // 👉 Let SkillModal own add/update success/error toasts.
  //    We only perform the save and refresh here.
  const handleModalSave = async (payload, id) => {
    if (offline) throw new Error("Offline mode");
    if (id) {
      await updateSkill(id, payload); // throws on error → SkillModal shows red toast
    } else {
      await addSkill(payload); // throws on error → SkillModal shows red toast
    }
    await loadSkills(false);
    return true;
  };

  const requestDelete = (skill) => {
    if (offline) return toast.warn("Can't delete while offline");
    setSkillToDelete(skill);
    setConfirmOpen(true);
  };
  const confirmDelete = async () => {
    try {
      await deleteSkill(skillToDelete.id);
      toast.success("Skill deleted", { autoClose: 1400 });
      await loadSkills(false);
    } catch (err) {
      toast.error(err.message || "Failed to delete skill");
    } finally {
      setConfirmOpen(false);
      setSkillToDelete(null);
    }
  };

  const grouped = skills.reduce((acc, s) => {
    const cat = s.category || "Other";
    if (!acc[cat]) acc[cat] = [];
    acc[cat].push(s);
    return acc;
  }, {});
  const categories = Object.keys(grouped).sort();

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
        <p>Loading skills...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {offline && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-amber-800 text-sm">
          Offline mode — showing last cached data. Edits disabled.
        </div>
      )}

      <div className="flex items-center justify-between mb-4">
        <h2 className="text-2xl font-semibold">Skills</h2>
        <div className="flex gap-2">
          <button
            onClick={handleRefresh}
            disabled={refreshing}
            className={`px-3 py-2 rounded text-sm ${
              refreshing
                ? "bg-gray-300 text-gray-700 cursor-not-allowed"
                : "bg-blue-600 text-white hover:bg-blue-700"
            }`}
          >
            {refreshing ? "Refreshing..." : "↻ Refresh"}
          </button>
          <button
            onClick={openNewModal}
            className={`px-3 py-2 rounded text-sm text-white ${
              offline
                ? "bg-gray-300 cursor-not-allowed"
                : "bg-green-600 hover:bg-green-700"
            }`}
          >
            + New Skill
          </button>
        </div>
      </div>

      {categories.length === 0 ? (
        <div className="text-gray-500">No skills found.</div>
      ) : (
        categories.map((category) => {
          const categoryColor = getColorForCategory(category);
          return (
            <div key={category} className="rounded-2xl p-4 bg-white shadow">
              <div
                className="flex items-center justify-between cursor-pointer"
                onClick={() => toggleCategory(category)}
              >
                <div className="flex items-center gap-3">
                  <div className="w-1.5 h-7 rounded bg-gray-200" />
                  <div className="flex items-center gap-2">
                    <span
                      className="w-3 h-3 rounded-full"
                      style={{ backgroundColor: categoryColor }}
                    />
                    <h3 className="text-lg font-semibold text-gray-900">
                      {category}
                    </h3>
                    <div className="text-sm text-gray-500">
                      ({grouped[category]?.length || 0})
                    </div>
                  </div>
                </div>
                <div className="text-gray-700 text-sm">
                  {expanded[category] ? "▲ Collapse" : "▼ Expand"}
                </div>
              </div>

              {expanded[category] && (
                <div className="mt-4 rounded-lg p-3 bg-white">
                  <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
                    {grouped[category].map((s, idx) => {
                      const skillColor = resolveColor(s.color) || categoryColor;
                      return (
                        <motion.div
                          key={s.id ?? s.name}
                          initial={{ opacity: 0, y: 10 }}
                          animate={{ opacity: 1, y: 0 }}
                          transition={{ delay: idx * 0.03 }}
                          className="relative bg-white border rounded-lg p-2 flex flex-col gap-2 shadow-sm"
                        >
                          <div
                            style={{
                              position: "absolute",
                              left: 0,
                              top: 0,
                              bottom: 0,
                              width: 8,
                              borderTopLeftRadius: 10,
                              borderBottomLeftRadius: 10,
                              backgroundColor: skillColor,
                            }}
                          />
                          <div style={{ paddingLeft: 12 }}>
                            <div className="flex items-center justify-between gap-2">
                              <div className="flex items-center gap-2">
                                <div
                                  className="w-3 h-3 rounded-full border"
                                  style={{ backgroundColor: skillColor }}
                                  title={skillColor}
                                />
                                <div className="font-medium text-sm text-gray-900 truncate">
                                  {s.name}
                                </div>
                              </div>
                              <div className="flex items-center gap-2">
                                <button
                                  onClick={() => openEditModal(s)}
                                  disabled={offline}
                                  className={`text-xs px-2 py-1 rounded ${
                                    offline
                                      ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                                      : "bg-indigo-50 text-indigo-700"
                                  }`}
                                >
                                  Edit
                                </button>
                                <button
                                  onClick={() => requestDelete(s)}
                                  disabled={offline}
                                  className={`text-xs px-2 py-1 rounded ${
                                    offline
                                      ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                                      : "bg-red-50 text-red-700"
                                  }`}
                                >
                                  Delete
                                </button>
                              </div>
                            </div>

                            <div className="text-xs text-gray-600 flex justify-between mt-1">
                              <div>{s.yearsOfExperience ?? 0} yrs</div>
                              <div>Last used: {s.lastUsed ?? "—"}</div>
                            </div>

                            <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden mt-2">
                              <div
                                className="h-2 rounded-full transition-all duration-900 ease-out"
                                style={{
                                  width: `${s.proficiency ?? 0}%`,
                                  background: skillColor,
                                }}
                              />
                            </div>

                            <div className="text-right text-xs text-gray-500 mt-1">
                              {s.proficiency ?? 0}%
                            </div>
                          </div>
                        </motion.div>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          );
        })
      )}

      <SkillModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSave={handleModalSave}
        skill={editingSkill}
        existingCategories={Object.keys(grouped)}
      />

      <ConfirmDialog
        open={confirmOpen}
        title="Delete Skill"
        message={`Delete "${skillToDelete?.name}"? This cannot be undone.`}
        onConfirm={confirmDelete}
        onCancel={() => {
          setConfirmOpen(false);
          setSkillToDelete(null);
        }}
      />
    </div>
  );
}
