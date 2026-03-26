import React from "react";

export default function ProjectViewModal({
  isOpen,
  onClose,
  project,
  isLoading,
}) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl p-6 relative animate-fadeIn">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
        >
          ✕
        </button>

        <h3 className="text-lg font-semibold mb-4 border-b pb-2">
          {project ? project.title : "Project Details"}
        </h3>

        {/* Loading State */}
        {isLoading && (
          <div className="flex justify-center items-center py-10">
            <div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full"></div>
          </div>
        )}

        {/* Error / Empty State */}
        {!isLoading && !project && (
          <div className="text-center text-gray-500 py-6">
            Unable to load project details.
          </div>
        )}

        {/* Details */}
        {!isLoading && project && (
          <div className="space-y-3 text-sm text-gray-700">
            <div>
              <strong className="text-gray-900">Description:</strong>{" "}
              <span>{project.description || "No description"}</span>
            </div>

            <div>
              <strong className="text-gray-900">Status:</strong>{" "}
              <span>{project.status || "—"}</span>
            </div>

            <div>
              <strong className="text-gray-900">Tech Stack:</strong>{" "}
              {project.techStack ? (
                <span className="inline-block">
                  {Array.isArray(project.techStack)
                    ? project.techStack.join(", ")
                    : project.techStack}
                </span>
              ) : (
                "—"
              )}
            </div>

            <div className="flex gap-4">
              <div>
                <strong className="text-gray-900">Start:</strong>{" "}
                <span>{project.startDate || "—"}</span>
              </div>
              <div>
                <strong className="text-gray-900">End:</strong>{" "}
                <span>{project.endDate || "—"}</span>
              </div>
            </div>

            {project.githubLink && (
              <div>
                <strong className="text-gray-900">GitHub:</strong>{" "}
                <a
                  href={project.githubLink}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-500 hover:underline"
                >
                  {project.githubLink}
                </a>
              </div>
            )}
          </div>
        )}

        {/* Footer */}
        <div className="flex justify-end mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded text-sm"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
