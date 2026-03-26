import { useEffect, useState, useRef } from "react";
import { toast } from "react-toastify";

/**
Props:
 - open
 - onClose()
 - onSave(payload, id)    // should return a promise (parent will await)
 - skill (optional)       // skill object when editing
 - existingCategories []  // optional array of strings
 - onAddCategory(cat)     // optional callback
 - onDeleteCategory(cat)  // optional callback
*/
export default function SkillModal({
  open,
  onClose,
  onSave,
  skill,
  existingCategories = [],
  onAddCategory,
  onDeleteCategory,
}) {
  const [form, setForm] = useState({
    id: null,
    name: "",
    category: "",
    proficiency: "",
    yearsOfExperience: "",
    lastUsed: "",
    color: "#3b82f6",
  });

  const [categories, setCategories] = useState(existingCategories || []);
  const [newCategory, setNewCategory] = useState("");
  const [saving, setSaving] = useState(false);

  const prevCatsRef = useRef(JSON.stringify(existingCategories || []));
  useEffect(() => {
    const next = JSON.stringify(existingCategories || []);
    if (next !== prevCatsRef.current) {
      prevCatsRef.current = next;
      setCategories(existingCategories || []);
    }
  }, [existingCategories]);

  useEffect(() => {
    if (!open) return;
    if (skill) {
      setForm({
        id: skill.id ?? null,
        name: skill.name ?? "",
        category: skill.category ?? existingCategories[0] ?? "",
        proficiency: skill.proficiency ?? "",
        yearsOfExperience: skill.yearsOfExperience ?? "",
        lastUsed: skill.lastUsed ?? "",
        color: skill.color ?? "#3b82f6",
      });
    } else {
      setForm({
        id: null,
        name: "",
        category: existingCategories[0] ?? "",
        proficiency: "",
        yearsOfExperience: "",
        lastUsed: "",
        color: "#3b82f6",
      });
    }
    setNewCategory("");
  }, [open, skill, existingCategories]);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  }
  function handleNumberChange(e) {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value === "" ? "" : Number(value) }));
  }

  function handleAddCategory() {
    const trimmed = (newCategory || "").trim();
    if (!trimmed) {
      toast.warn("Enter a category name");
      return;
    }
    if (!categories.includes(trimmed)) {
      const next = [...categories, trimmed];
      setCategories(next);
      if (typeof onAddCategory === "function") {
        try {
          onAddCategory(trimmed);
        } catch (err) {
          console.error("onAddCategory threw:", err);
        }
      }
      toast.success("Category added");
    } else {
      toast.info("Category already exists");
    }
    setForm((f) => ({ ...f, category: trimmed }));
    setNewCategory("");
  }

  function handleDeleteCategoryLocal(cat) {
    if (!cat) return;
    const next = categories.filter((c) => c !== cat);
    setCategories(next);
    if (typeof onDeleteCategory === "function") {
      try {
        onDeleteCategory(cat);
      } catch (err) {
        console.error("onDeleteCategory threw:", err);
      }
    }
    setForm((f) => (f.category === cat ? { ...f, category: "" } : f));
    toast.info(`Removed category "${cat}"`);
  }

  async function handleSave() {
    if (!form.name || !form.name.trim()) {
      toast.warn("Skill name is required");
      return;
    }

    const payload = {
      name: form.name.trim(),
      category: form.category || null,
      proficiency: form.proficiency === "" ? null : Number(form.proficiency),
      yearsOfExperience:
        form.yearsOfExperience === "" ? null : Number(form.yearsOfExperience),
      lastUsed: form.lastUsed || null,
      color: form.color || null,
    };

    try {
      setSaving(true);
      // ✅ Wrap onSave in try/catch to only show toast if success
      await onSave(payload, form.id);
      toast.success(form.id ? "Skill updated" : "Skill created");
      onClose && onClose();
    } catch (err) {
      console.error("Save failed", err);
      toast.error(err.message || "Failed to save skill");
    } finally {
      setSaving(false);
    }
  }

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-2xl p-6">
        <h3 className="text-lg font-semibold mb-4">
          {form.id ? "Edit Skill" : "Add Skill"}
        </h3>

        <div className="grid grid-cols-1 gap-3">
          {/* name */}
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Skill Name *
            </label>
            <input
              name="name"
              value={form.name}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 font-semibold"
              placeholder="e.g. React"
            />
          </div>

          {/* category with add */}
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Category
            </label>
            <div className="flex gap-2">
              <select
                name="category"
                value={form.category || ""}
                onChange={handleChange}
                className="flex-1 border rounded px-3 py-2"
              >
                <option value="">Select category</option>
                {categories.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>

              <input
                value={newCategory}
                onChange={(e) => setNewCategory(e.target.value)}
                placeholder="New category"
                className="border rounded px-2 py-1"
              />
              <button
                onClick={handleAddCategory}
                type="button"
                className="px-3 py-2 bg-green-600 text-white rounded"
              >
                Add
              </button>
            </div>

            {/* deletable chips */}
            <div className="flex flex-wrap gap-2 mt-2">
              {categories.map((c) => (
                <div
                  key={c}
                  className="flex items-center gap-2 bg-gray-100 px-2 py-1 rounded"
                >
                  <span className="text-sm">{c}</span>
                  <button
                    onClick={() => handleDeleteCategoryLocal(c)}
                    type="button"
                    className="text-xs text-red-500 px-1"
                  >
                    Delete
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* two column numeric */}
          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Proficiency (%)
              </label>
              <input
                name="proficiency"
                value={form.proficiency ?? ""}
                onChange={(e) => handleChange(e)}
                type="number"
                min="0"
                max="100"
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Years
              </label>
              <input
                name="yearsOfExperience"
                value={form.yearsOfExperience ?? ""}
                onChange={handleNumberChange}
                type="number"
                min="0"
                className="w-full border rounded px-3 py-2"
              />
            </div>
          </div>

          {/* last used */}
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Last Used
            </label>
            <input
              name="lastUsed"
              value={form.lastUsed ?? ""}
              onChange={handleChange}
              type="date"
              className="w-full border rounded px-3 py-2"
            />
          </div>

          {/* color */}
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Color Tag (optional)
            </label>
            <input
              name="color"
              value={form.color}
              onChange={handleChange}
              type="color"
              className="w-16 h-9 border rounded"
            />
          </div>
        </div>

        <div className="flex justify-end gap-3 mt-4">
          <button
            onClick={onClose}
            className="px-4 py-2 border rounded"
            disabled={saving}
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="px-4 py-2 bg-blue-600 text-white rounded"
            disabled={saving}
          >
            {saving ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
