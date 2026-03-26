import React, { useState } from "react";
import { toast } from "react-toastify";
import {
  FaEnvelope,
  FaLinkedin,
  FaGithub,
  FaExclamationTriangle,
} from "react-icons/fa";

const Contact = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    message: "",
  });
  const [loading, setLoading] = useState(false);
  const [backendDown, setBackendDown] = useState(false);

  const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:1907/pdbapp/api";

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.name || !formData.email || !formData.message) {
      toast.error("Please fill in all fields.", { theme: "colored" });
      return;
    }

    if (!validateEmail(formData.email)) {
      toast.error("Please enter a valid email address.", { theme: "colored" });
      return;
    }

    setLoading(true);

    try {
      const res = await fetch(`${API_BASE_URL}/contact`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (!res.ok) throw new Error("Network response was not ok");

      toast.success("Message sent successfully!", { theme: "colored" });
      setFormData({ name: "", email: "", message: "" });
      setBackendDown(false);
    } catch (err) {
      console.error("Error submitting form:", err);
      setBackendDown(true);
      toast.warn("Backend unreachable. Please use the email link below.", {
        theme: "colored",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 md:p-8 max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-2">Contact Me</h1>
      <p className="text-gray-500 mb-8">
        I'd love to hear from you! Whether it’s a project idea, feedback, or
        just a hello — drop me a message below.
      </p>

      {/* 🟡 Backend Down Animated Notice */}
      {backendDown && (
        <div className="relative mb-6 overflow-hidden rounded-lg border border-amber-300 bg-gradient-to-r from-amber-50 to-yellow-50 shadow-sm animate-fadeIn">
          <div className="flex items-start gap-3 px-4 py-3 text-amber-800">
            <FaExclamationTriangle className="mt-0.5 text-amber-500 flex-shrink-0 animate-pulse" />
            <div className="text-sm leading-relaxed">
              <strong>Service temporarily unavailable.</strong> You can reach me
              directly at{" "}
              <a
                href="mailto:arthur.sj.salla@gmail.com"
                className="text-blue-700 font-medium underline hover:text-blue-900 transition-colors"
              >
                arthur.sj.salla@gmail.com
              </a>
              .
            </div>
          </div>
        </div>
      )}

      {/* Contact Form */}
      <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Name
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="Your name"
            />
          </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Email
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="you@example.com"
            />
          </div>

          {/* Message */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Message
            </label>
            <textarea
              name="message"
              rows="4"
              value={formData.message}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
              placeholder="Type your message here..."
            />
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading}
            className={`w-full py-2 px-4 font-semibold rounded-lg text-white transition-all ${
              loading
                ? "bg-blue-300 cursor-not-allowed"
                : "bg-blue-600 hover:bg-blue-700"
            }`}
          >
            {loading ? "Sending..." : "Send Message"}
          </button>
        </form>
      </div>

      {/* Social Links */}
      <div className="mt-10 text-center text-gray-600 space-y-2">
        <p className="flex items-center justify-center text-sm transition-all hover:text-blue-700">
          <FaLinkedin className="text-blue-700 mr-2 transition-transform duration-200 hover:scale-110" />
          <a
            href="https://linkedin.com/in/arthursalla"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:underline transition-all"
          >
            linkedin.com/in/arthursalla
          </a>
        </p>

        <p className="flex items-center justify-center text-sm transition-all hover:text-gray-800">
          <FaGithub className="text-gray-800 mr-2 transition-transform duration-200 hover:scale-110" />
          <a
            href="https://github.com/artsal"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:underline transition-all"
          >
            github.com/artsal
          </a>
        </p>
      </div>
    </div>
  );
};

export default Contact;
