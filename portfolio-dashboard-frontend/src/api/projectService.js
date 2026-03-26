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

// CREATE A NEW PROJECT
export const createProject = async (project) => {
  try {
    const response = await fetch(`${API_BASE_URL}/projects`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeader() },
      body: JSON.stringify(project),
    });
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error("You don’t have permission to modify content.");
      }
      throw new Error("Failed to create project.");
    }
    return await response.json();
  } catch (error) {
    console.error("Error creating a project:", error);
    throw error;
  }
};

// UPDATE AN EXISTING PROJECT
export const updateProject = async (id, project) => {
  try {
    const response = await fetch(`${API_BASE_URL}/projects/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json", ...getAuthHeader() },
      body: JSON.stringify(project),
    });
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error("You don’t have permission to modify content.");
      }
      throw new Error("Failed to update project.");
    }
    return await response.json();
  } catch (error) {
    console.error("Error updating the project:", error);
    throw error;
  }
};

// FETCH ALL PROJECTS
export const fetchProjects = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/projects`);
    if (!response.ok) {
      throw new Error("Failed to fetch projects");
    }
    return await response.json();
  } catch (error) {
    console.error("Error fetching projects:", error);
    throw error;
  }
};

// FETCH PROJECT BY ID
export const fetchProjectById = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/projects/${id}`);
    if (!response.ok) {
      const msg = await response.text();
      throw new Error(msg || `Failed to fetch project ${id}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Error fetching project by Id:", error);
    throw error;
  }
};

// DELETE A PROJECT
export const deleteProject = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/projects/${id}`, {
      method: "DELETE",
      headers: { ...getAuthHeader() },
    });
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error("You don’t have permission to modify content.");
      }
      throw new Error("Failed to delete project.");
    }
  } catch (error) {
    console.error("Error deleting the project:", error);
    throw error;
  }
};
