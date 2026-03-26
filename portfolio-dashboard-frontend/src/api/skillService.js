const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:1907/pdbapp/api";

// 🔒 Helper to include Basic Auth header if admin logged in
function getAuthHeader() {
  const creds = localStorage.getItem("portfolio_admin_v1");
  if (!creds) return {};
  try {
    const { username, password } = JSON.parse(creds);
    return { Authorization: "Basic " + btoa(`${username}:${password}`) };
  } catch {
    return {};
  }
}

// 🟦 READ — Fetch all skills
export async function fetchSkills() {
  try {
    const response = await fetch(`${API_BASE_URL}/skills`);
    if (!response.ok) throw new Error("Failed to fetch skills");
    return await response.json();
  } catch (error) {
    console.error("Error fetching skills:", error);
    throw error;
  }
}

// 🟩 CREATE — Add a new skill
export async function addSkill(skillData) {
  try {
    const response = await fetch(`${API_BASE_URL}/skills`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeader() },
      body: JSON.stringify(skillData),
    });
    // 🧠 Friendly error message handling
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error("You don’t have permission to modify content.");
      }
      throw new Error("Failed to add skill.");
    }
    return await response.json();
  } catch (error) {
    console.error("Error adding skill:", error);
    throw error;
  }
}

// 🟨 UPDATE — Update an existing skill
export async function updateSkill(id, skillData) {
  try {
    const response = await fetch(`${API_BASE_URL}/skills/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json", ...getAuthHeader() },
      body: JSON.stringify(skillData),
    });
    // 🧠 Friendly error message handling
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error("You don’t have permission to modify content.");
      }
      throw new Error("Failed to update skill.");
    }
    return await response.json();
  } catch (error) {
    console.error("Error updating skill:", error);
    throw error;
  }
}

// 🟥 DELETE — Delete a skill
export async function deleteSkill(id) {
  try {
    const response = await fetch(`${API_BASE_URL}/skills/${id}`, {
      method: "DELETE",
      headers: { ...getAuthHeader() },
    });
    // 🧠 Friendly error message handling
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error("You don’t have permission to modify content.");
      }
      throw new Error("Failed to delete skill.");
    }
    return true;
  } catch (error) {
    console.error("Error deleting skill:", error);
    throw error;
  }
}
