# NyayaLens Product Requirements Document (PRD)

## 1. Executive Summary & Vision
**Project Name**: NyayaLens  
**Tagline**: *Understand the law. Verify the evidence. Know your next step.*  
**Mission**: Democratize legal literacy and access to justice across India by delivering a competition-grade, GenAI-assisted legal navigation platform that guarantees zero-hallucination evidence grounding, strict privacy safeguards, and actionable procedural next steps.

---

## 2. Problem Statement & Opportunities
1. **Asymmetry of Legal Knowledge**: Everyday citizens, tenants, employees, and MSME founders sign contracts containing dense legal terminology (e.g., indemnity, liquidated damages, unilateral termination, perpetual restrictive covenants) without understanding the operational risks.
2. **AI Hallucination Risk in Law**: Generic LLMs frequently invent judicial citations, misquote statutory provisions, or assume facts not present in the user's contract.
3. **Data Privacy & Breach Anxiety**: Uploading contracts with Aadhaar, PAN, phone numbers, and financial details to third-party cloud LLMs poses severe regulatory and privacy hazards under India's Digital Personal Data Protection (DPDP) Act, 2023.
4. **Action Paralysis**: After realizing a dispute or unfavorable clause exists, citizens do not know how to proceed—what evidence to preserve, what questions to ask a lawyer, or which official legal aid authorities to approach.

---

## 3. Target User Personas
- **Rohit (Employee / Freelancer)**: Received an abrupt termination notice with zero severance pay, contradicting the 60-day notice clause in his employment agreement.
- **Priya (Tenant)**: Facing an unexpected 25% rent hike from her landlord, despite an 11-month lease capping increases at 5%.
- **Ananya (Startup Founder)**: Reviewing an aggressive unilateral vendor NDA that imposes perpetual confidentiality and unilateral indemnification.

---

## 4. Key Functional Pillars

### Pillar 1: Evidence-Grounded Analysis Workspace
- 3-Column layout: Document / Clause Navigator (left), Analysis Stream (center), and Verbatim Evidence Inspector (right).
- Plain-language executive summary with toggleable definitions for complex terms.
- Deterministic and GenAI contractual risk scanning with severity badges (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).
- Every assertion is linked to an exact clause, page number, confidence percentage, and verification status (`VERIFIED`, `INFERRED`, `NOT_FOUND`, `NEEDS_REVIEW`).

### Pillar 2: Semantic Document Comparison
- Compare base contracts against termination notices, escalations, or counter-party drafts.
- Categorizes deltas into `ADDED`, `REMOVED`, `MODIFIED`, and `UNCHANGED`.
- Generates impact assessments and safe procedural recommendations.

### Pillar 3: Grounded Conversational Q&A ("Ask My Document")
- Query documents using natural language queries.
- Verbatim clause citations with page tracking.
- **Transparency Audit**: Explicitly surfaces "What was NOT found in the document", eliminating false assumptions.

### Pillar 4: ActionPath & Consultation Preparation
- Chronological, prioritized procedural checklist (document preservation, formal correspondence, dispute escalation).
- Strategic "Questions to Ask Qualified Legal Counsel" mapped to specific contract clauses.
- One-click print/export consultation brief.

### Pillar 5: Official Indian Legal Portals Directory
- Curated index of sovereign repositories (India Code, Supreme Court of India judgments, eCourts Services, NALSA legal aid, SAMADHAN labor conciliation).

### Pillar 6: Zero-Retention Privacy & Threat Model
- Regex masking of Aadhaar, PAN, phone, email, and bank account numbers prior to LLM processing.
- Strictly in-memory cache architecture with instantaneous session data wipe button.
- Prompt injection defense using `<UNTRUSTED_DOCUMENT_CONTENT>` encapsulation.

---

## 5. Non-Functional & Technical Requirements
- **Frontend**: React 19, TypeScript, Vite, Vanilla CSS Design System, WCAG 2.1 AA accessible, Trilingual (`en`, `hi`, `te`).
- **Backend**: Spring Boot 3.4, Java 21, Apache PDFBox 3.0.4.
- **Dual AI Engine**: Gemini 3.8 Flash (rapid ingestion, Q&A) + Gemini 3.1 Pro Preview (semantic reasoning, diff).
- **Fallback Reliability**: 100% deterministic offline fallback engine ensuring zero downtime or blank screens during judging.
- **Repository Size Guard**: Strictly < 10 MB total size.
