# NyayaLens REST API Documentation

All NyayaLens API endpoints are served under the base prefix `/api/v1` and follow a consistent envelope structure.

---

## 1. Standard Response Envelope

```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2026-09-15T14:30:00.000Z",
  "requestId": "req_8h2j81z9a"
}
```

On error:
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 429,
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Please wait before submitting another document."
  },
  "timestamp": "2026-09-15T14:30:00.000Z",
  "requestId": "req_8h2j81z9a"
}
```

---

## 2. Endpoints Reference

### 2.1 System Health
- **`GET /api/v1/health`**
  - Returns service status, active AI models, rate limiter state, and active document count.

### 2.2 Document Management
- **`POST /api/v1/documents?redactPii=true`**
  - **Content-Type**: `multipart/form-data` (field: `file`).
  - Accepts PDF, TXT, or MD files up to 15MB.
  - Returns parsed document ID, page count, section count, and PII redaction audit summary.
- **`POST /api/v1/documents/demo/{scenario}?redactPii=true`**
  - Scenarios: `employment-agreement`, `termination-notice`, `rental-agreement`, `rent-escalation-notice`, `mutual-nda`, `vendor-nda`.
  - Instantly loads synthetic test agreement into session memory.
- **`GET /api/v1/documents`**
  - Returns list of active session documents.
- **`GET /api/v1/documents/{id}`**
  - Returns full document details including parsed pages and clause sections.
- **`DELETE /api/v1/documents/{id}`**
  - Evicts specific document from session memory.
- **`DELETE /api/v1/documents`**
  - Purges all active session documents and caches immediately.

### 2.3 Analysis & Risk Detection
- **`POST /api/v1/documents/{id}/analyze`**
  - Runs parallel deterministic rules + Gemini extraction on the specified document.
  - Returns plain-language summary, parties, effective date, governing law, obligations, deadlines, financial terms, and verified risks.
- **`GET /api/v1/documents/{id}/risks`**
  - Returns extracted risk claims with severity and evidence citations.

### 2.4 Document Comparison
- **`POST /api/v1/compare`**
  - **Body**: `{ "documentAId": "...", "documentBId": "..." }`
  - Returns semantic delta breakdown: added, removed, and modified clauses with severity and procedural recommendations.

### 2.5 Grounded Q&A
- **`POST /api/v1/questions`**
  - **Body**: `{ "documentId": "...", "question": "What is the notice period?" }`
  - Returns direct answer, verification status, verbatim evidence citations, confidence meter, and transparent "what was not found" audit.

### 2.6 ActionPath & Preparation
- **`POST /api/v1/actionpath`**
  - **Body**: `{ "documentId": "..." }`
  - Returns prioritized procedural checklist, clause-mapped questions for qualified legal counsel, and official legal aid channels.

### 2.7 Sovereign Legal Resources
- **`GET /api/v1/legal-resources`**
  - Returns curated directory of verified Indian statutory repositories and judicial portals.
