# NyayaLens Deployment Guide

NyayaLens supports two primary deployment topologies:
1. **Unified Docker Container**: Single lightweight container serving the Spring Boot API and pre-built React static assets. Ideal for Render, Railway, Fly.io, or AWS ECS.
2. **Decoupled Local Development**: Spring Boot backend running on `:8080` with Vite HMR dev server running on `:5173`.

---

## 1. One-Click Render Deployment

NyayaLens includes a production-ready `render.yaml` blueprint:

1. Push your repository to GitHub.
2. Log in to [Render.com](https://render.com).
3. Click **New +** -> **Blueprint**.
4. Connect the repository; Render will automatically detect `render.yaml`.
5. Under Environment Variables, set `GEMINI_API_KEY` (optional; if omitted, the high-fidelity offline fallback engine will automatically activate).
6. Click **Apply**. Render will run the multi-stage `Dockerfile` and provision the live service.

---

## 2. Docker Deployment

### Build the Image
```bash
docker build -t nyayalens:latest .
```

### Run the Container
```bash
docker run -d \
  -p 8080:8080 \
  -e GEMINI_API_KEY="your-gemini-api-key" \
  -e SPRING_PROFILES_ACTIVE="default" \
  --name nyayalens-app \
  nyayalens:latest
```

Access the application at `http://localhost:8080`.

---

## 3. Local Development Setup

### Prerequisites
- Java 21+ (Eclipse Temurin or Oracle JDK)
- Node.js 20+ & npm
- Maven 3.9+

### Step 1: Start Backend (Terminal 1)
```bash
cd backend
mvn spring-boot:run
```
The backend starts at `http://localhost:8080`.

### Step 2: Start Frontend (Terminal 2)
```bash
cd frontend
npm install
npm run dev
```
The Vite development server starts at `http://localhost:5173` with automatic `/api` proxying to `http://localhost:8080`.

---

## 4. Environment Variables Reference

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `GEMINI_API_KEY` | *(empty)* | Google Gemini API key. If absent, deterministic fallback engine activates. |
| `GEMINI_MODEL_FAST` | `gemini-3.8-flash` | Model used for extraction, summary, and Q&A. |
| `GEMINI_MODEL_REASONING` | `gemini-3.1-pro-preview` | Model used for multi-document comparison and synthesis. |
| `SPRING_PROFILES_ACTIVE` | `default` | Spring profile (`default` for in-memory, `mysql` for relational). |
| `RATE_LIMIT_ENABLED` | `true` | Enables token-bucket rate limiting filter. |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Allowed CORS origins separated by commas. |
