import React from "react";

const colorClasses = {
  blue: "bg-blue-100 text-blue-800",
  green: "bg-green-100 text-green-800",
  yellow: "bg-yellow-100 text-yellow-800",
  purple: "bg-purple-100 text-purple-800",
};

function Card({ title, value, icon, color, children, className = "" }) {
  // 🟩 Generic wrapper mode (for charts, custom content)
  if (children) {
    return (
      <div
        className={`bg-white p-5 rounded-2xl shadow border border-gray-100 hover:shadow-lg transition duration-200 ${className}`}
        style={{ minHeight: "120px" }}
      >
        {children}
      </div>
    );
  }

  // 🟦 Default data card mode (for overview stats)
  return (
    <div
      className={`p-5 rounded-2xl shadow border border-gray-100 ${colorClasses[color]}`}
      style={{ minHeight: "120px" }}
    >
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold">{title}</h2>
          <p className="text-2xl font-bold mt-1">{value}</p>
        </div>
        <div className="text-3xl">{icon}</div>
      </div>
    </div>
  );
}

export default Card;
