import React, { useEffect, useState } from "react";
import {
  FaProjectDiagram,
  FaCogs,
  FaClock,
  FaCertificate,
} from "react-icons/fa";
import { apiFetch } from "../api/api";
import ProjectsBarChart from "../charts/ProjectsBarChart";
import SkillsPieChart from "../charts/SkillsPieChart";
import Card from "../components/Card";

const Overview = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [fromCache, setFromCache] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const { data, fromCache } = await apiFetch("/overview/stats");
        setStats(data);
        setFromCache(fromCache);
        setError(null);
      } catch (err) {
        console.error("Overview fetch failed", err);
        setError("Backend unreachable, showing demo data.");
        // fallback
        setStats({
          projects: { latest: "Demo Project", count: 1 },
          skills: { top: ["React", "Java", "MySQL"], count: 6 },
          experience: { years: 16 },
          certifications: { latest: "None", count: 0 },
        });
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  if (loading)
    return (
      <div className="flex items-center justify-center h-[60vh] text-gray-500">
        Loading overview...
      </div>
    );

  const { projects, skills, experience, certifications } = stats || {};

  const cards = [
    {
      title: "Projects",
      main: projects?.latest ?? "No Projects Yet",
      sub: "Latest Project",
      footer: `${projects?.count ?? 0} total projects`,
      icon: <FaProjectDiagram />,
      color: "blue",
    },
    {
      title: "Skills",
      main: skills?.top?.join(", ") || "No Skills Added",
      sub: "Top Skills",
      footer: `${skills?.count ?? 0} total skills`,
      icon: <FaCogs />,
      color: "green",
    },
    {
      title: "Experience",
      main: `${experience?.years ?? 0} Years`,
      sub: "Industry Experience",
      footer: "Professional experience summary",
      icon: <FaClock />,
      color: "yellow",
    },
    {
      title: "Certifications",
      main: certifications?.latest ?? "None",
      sub: "Latest Certification",
      footer: `${certifications?.count ?? 0} total certifications`,
      icon: <FaCertificate />,
      color: "purple",
    },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-semibold text-gray-800">
          Dashboard Overview
        </h1>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-amber-800 text-sm">
          {error}
        </div>
      )}
      {fromCache && !error && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-amber-800 text-sm">
          Offline mode — showing last saved data.
        </div>
      )}

      <p className="text-gray-500 mb-6">
        A quick summary of my portfolio highlights and progress.
      </p>

      {/* Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
        {cards.map((card, i) => (
          <Card
            key={i}
            title={card.title}
            value={
              <div>
                <div className="text-xl font-semibold text-gray-900">
                  {card.main}
                </div>
                <div className="text-sm text-gray-600">{card.sub}</div>
                <div className="text-xs text-gray-400 mt-2 italic">
                  {card.footer}
                </div>
              </div>
            }
            icon={card.icon}
            color={card.color}
          />
        ))}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <h3 className="text-lg font-semibold mb-3 text-gray-700">
            Projects Overview
          </h3>
          <ProjectsBarChart />
        </Card>

        <Card>
          <h3 className="text-lg font-semibold mb-3 text-gray-700">
            Skills Overview
          </h3>
          <SkillsPieChart />
        </Card>
      </div>
    </div>
  );
};

export default Overview;
