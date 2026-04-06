# 🚀 Portfolio Dashboard

> A full-stack personal portfolio application built with **React + Spring Boot**, designed to showcase projects, skills, and achievements in an interactive, professional way.

---

## 📖 Overview

**Portfolio Dashboard** goes beyond a static resume site — it’s a living web app featuring admin-only CRUD management, dynamic UI updates, secure backend endpoints, and a real contact form that sends messages directly to your inbox.

This project was created to learn, experiment, and deploy a fully functional, production-ready portfolio from scratch.

---

## 🧱 Tech Stack

**Frontend**
- React (Vite)
- Tailwind CSS
- Toastify
- Framer Motion

**Backend**
- Spring Boot (Java 17)
- RESTful APIs
- Spring Mail (JavaMailSender)
- MySQL Database

**Deployment**
- Railway (Backend)
- Netlify (Frontend)

---

## ⚙️ Key Features

### 🔐 Admin Control
- Hidden **Admin Login** accessible from the footer  
- Admin-only CRUD for **Projects** and **Skills**
- Unauthorized users see friendly toast messages: “Only admins can modify content.”

### 🎨 Dynamic & Responsive UI
- Clean dashboard layout with collapsible skill categories  
- Animated transitions powered by **Framer Motion**  
- Mobile-friendly design using **Tailwind CSS**

### 💬 Real Contact Integration
- Live **contact form** connected to Gmail via **Spring Boot Mail**
- Sends messages straight to the owner’s inbox  
- Fallback toast + on-screen notice when backend is unreachable

### ⚡ Performance & Offline
- Smart local caching for faster reloads  
- Graceful fallback when backend API is unavailable

### 🔒 Security
- Environment variables for all credentials (`GMAIL_USERNAME`, `GMAIL_PASSWORD`)  
- Gmail App Password used — never stored in code

---

## 🧩 Architecture

