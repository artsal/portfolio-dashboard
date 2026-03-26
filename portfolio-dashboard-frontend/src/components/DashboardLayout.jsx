import { useState } from "react";
import {
  FaHome,
  FaProjectDiagram,
  FaTools,
  FaEnvelope,
  FaBars,
} from "react-icons/fa";
import { Link, NavLink, Outlet } from "react-router-dom";
import Footer from "./Footer"; // ✅ add footer import

function DashboardLayout() {
  const [isOpen, setIsOpen] = useState(true);

  return (
    <div className="flex min-h-screen bg-gray-100">
      {/* Sidebar */}
      <aside
        className={`${
          isOpen ? "w-64" : "w-16"
        } bg-white shadow-md p-4 transition-all duration-300 flex flex-col`}
      >
        {/* Header with title + Beta badge */}
        <div className="flex items-center justify-between mb-6">
          <Link to="/" className="flex items-center gap-2">
            {isOpen && (
              <h2 className="text-xl font-semibold hover:text-blue-600 transition flex items-center">
                My Portfolio
                <span className="ml-2 bg-yellow-200 text-yellow-900 text-[10px] font-bold px-2 py-[1px] rounded-full border border-yellow-300">
                  BETA
                </span>
              </h2>
            )}
          </Link>
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="text-gray-600 hover:text-gray-900"
            title="Toggle sidebar"
          >
            <FaBars size={20} />
          </button>
        </div>

        {/* Navigation links */}
        <nav className="space-y-2">
          {[
            { to: "/", icon: <FaHome />, label: "Overview", end: true },
            { to: "/projects", icon: <FaProjectDiagram />, label: "Projects" },
            { to: "/skills", icon: <FaTools />, label: "Skills" },
            { to: "/contact", icon: <FaEnvelope />, label: "Contact" },
          ].map(({ to, icon, label, end }, index) => (
            <NavLink
              key={index}
              to={to}
              end={end}
              className={({ isActive }) =>
                `relative flex items-center gap-3 p-2 rounded transition-all duration-300 ${
                  isActive
                    ? "bg-blue-50 text-blue-700 font-semibold"
                    : "hover:bg-gray-200 text-gray-700"
                }`
              }
            >
              {({ isActive }) => (
                <>
                  {isActive && (
                    <span className="absolute left-0 top-0 h-full w-1 bg-blue-600 rounded-r transition-all duration-300"></span>
                  )}
                  <span className="ml-2">{icon}</span>
                  {isOpen && <span className="ml-2">{label}</span>}
                </>
              )}
            </NavLink>
          ))}
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col p-6">
        <header className="mb-6 border-b pb-2">
          <h1 className="text-2xl font-bold">Dashboard</h1>
        </header>

        {/* Page content */}
        <div className="flex-grow">
          <Outlet />
        </div>

        {/* Footer now always visible */}
        <Footer />
      </main>
    </div>
  );
}

export default DashboardLayout;
