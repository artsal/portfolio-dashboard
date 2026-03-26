const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:1907/pdbapp/api";

export async function apiFetch(path, { method = "GET", body } = {}) {
  const key = `cache:${path}`;
  const ctrl = new AbortController();
  const t = setTimeout(() => ctrl.abort(), 8000); // 8s timeout

  try {
    const res = await fetch(`${API_BASE_URL}${path}`, {
      method,
      headers: body ? { "Content-Type": "application/json" } : undefined,
      body: body ? JSON.stringify(body) : undefined,
      signal: ctrl.signal,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    localStorage.setItem(key, JSON.stringify({ ts: Date.now(), data }));
    return { data, fromCache: false };
  } catch (err) {
    const cached = localStorage.getItem(key);
    if (cached) {
      const { data } = JSON.parse(cached);
      return { data, fromCache: true, error: err };
    }
    throw err;
  } finally {
    clearTimeout(t);
  }
}
