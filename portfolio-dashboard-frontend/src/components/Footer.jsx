import React, { useState } from "react";

const ADMIN_KEY = "portfolio_admin_v1";

const Footer = () => {
  const [showModal, setShowModal] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isAdminUser, setIsAdminUser] = useState(
    !!localStorage.getItem(ADMIN_KEY)
  );
  const [showTooltip, setShowTooltip] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      if (!username || !password) {
        setError("Please enter both username and password");
        return;
      }

      const res = await fetch("/api/auth/check", {
        headers: {
          Authorization: "Basic " + btoa(`${username}:${password}`),
        },
      });

      if (!res.ok) {
        setError("Invalid credentials");
        return;
      }

      localStorage.setItem(ADMIN_KEY, JSON.stringify({ username, password }));
      setIsAdminUser(true);
      setShowModal(false);
      setUsername("");
      setPassword("");
      setError("");
    } catch (err) {
      setError("Login failed. Please try again.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem(ADMIN_KEY);
    setIsAdminUser(false);
  };

  return (
    <footer className="border-t border-gray-200 mt-8 pt-4 pb-3 text-center text-sm text-gray-500 relative">
      <p className="mb-1">
        © {new Date().getFullYear()}{" "}
        <span className="font-medium text-gray-700">Arthur Salla</span>. All
        rights reserved.
      </p>
      <p className="text-xs text-gray-400 mb-1">
        Crafted with <span className="text-red-400">🔥</span> using{" "}
        <span className="font-medium text-gray-600">React</span> &{" "}
        <span className="font-medium text-gray-600">Spring Boot</span>.
      </p>

      {/* Admin control with tooltip */}
      <div className="relative flex justify-center">
        <button
          onMouseEnter={() => setShowTooltip(true)}
          onMouseLeave={() => setShowTooltip(false)}
          onClick={() => (isAdminUser ? handleLogout() : setShowModal(true))}
          className={`mt-2 px-3 py-1 text-xs rounded-md border transition ${
            isAdminUser
              ? "border-green-500 text-green-600 hover:bg-green-50"
              : "border-blue-500 text-blue-600 hover:bg-blue-50"
          }`}
        >
          {isAdminUser ? "Logout Admin" : "Admin Login"}
        </button>

        {/* Tooltip */}
        {showTooltip && (
          <div className="absolute bottom-full mb-1 px-2 py-1 bg-gray-800 text-white text-xs rounded shadow-lg">
            {isAdminUser
              ? "You are logged in as admin"
              : "Restricted access. Admins can modify content."}
          </div>
        )}
      </div>

      {/* Lightweight inline modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white p-5 rounded-lg shadow-md w-80 text-left relative">
            <h2 className="text-lg font-semibold mb-3 text-gray-700">
              Admin Login
            </h2>
            <form onSubmit={handleLogin}>
              <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full border border-gray-300 rounded-md px-3 py-2 mb-2 text-sm focus:outline-none focus:ring-1 focus:ring-blue-400"
              />
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full border border-gray-300 rounded-md px-3 py-2 mb-3 text-sm focus:outline-none focus:ring-1 focus:ring-blue-400"
              />
              {error && <p className="text-xs text-red-500 mb-2">{error}</p>}
              <div className="flex justify-end space-x-2">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setError("");
                  }}
                  className="text-sm text-gray-500 hover:underline"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="bg-blue-500 text-white px-3 py-1 rounded-md text-sm hover:bg-blue-600"
                >
                  Login
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </footer>
  );
};

export default Footer;
