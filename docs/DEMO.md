# NyayaLens Evaluator & Judge Walkthrough (2-Minute Script)

Welcome to the evaluator walkthrough for **NyayaLens**. This guide is designed to showcase all core innovations in under 2 minutes.

---

## ⏱ The 2-Minute Evaluator Journey

### Step 1: Instant Zero-Setup Launch (0:00 - 0:20)
1. Navigate to the **Dashboard** (`/`).
2. Under **Quick Demonstration Scenarios**, click **⚖️ Employment Dispute**.
3. *What happens*: The system parses a multi-page employment agreement, runs PII anonymization in-memory, executes deterministic risk detection, and routes you directly into the **Analysis Workspace**.

---

### Step 2: The Flagship 3-Column Analysis Workspace (0:20 - 0:50)
1. **Notice the Layout**:
   - **Left Pane**: Structured clause navigator (`Clause 8.2 Termination`, `Clause 9 Non-Compete`).
   - **Center Pane**: Plain-language summary, key dates, financial obligations, and prioritized risk scanner cards.
   - **Right Pane**: Verbatim Evidence Inspector with continuous confidence score meter and verification status badge (`VERIFIED` vs `NEEDS_REVIEW`).
2. **Interactive Evidence Grounding**:
   - In the center column, click the **CRITICAL RISK** card: *"Clause 8.2 requires 60 days notice or pay in lieu, but counterparty notice demands immediate departure."*
   - Observe how the right column immediately loads the verbatim contractual quote from Page 2, Clause 8.2, with a 95% confidence score.
3. **Accessibility & Plain-Language Toggle**:
   - In the top navigation bar, toggle **"Explain Simply"**.
   - Notice legal jargon (e.g. *indemnification*, *non-compete*, *unilateral termination*) dynamically displays accessible explanatory tooltips.
   - Switch language from **EN** to **हि (Hindi)** or **తె (Telugu)** to view localized interface labels.

---

### Step 3: Conversational Q&A with Transparency Audit (0:50 - 1:15)
1. Click **Ask My Document** in the top navigation.
2. Click the suggested chip: *"What is my notice period for resignation or termination?"*
3. **Key Evaluator Moment**: Notice the **Transparency Audit: What Was Not Found in Document** box. NyayaLens explicitly surfaces what the contract does *not* contain (e.g., absence of performance improvement notice periods), eliminating false assumptions.

---

### Step 4: Cross-Document Semantic Comparison (1:15 - 1:35)
1. Navigate to **Compare Documents** (`/compare`).
2. Click the preset button: **⚖️ Employment Agreement vs Termination Notice**.
3. Inspect the side-by-side delta view:
   - Base Agreement (Document A): 60-day notice period.
   - Termination Notice (Document B): Immediate departure with 0 severance pay.
   - Semantic Impact: Tagged as `CRITICAL IMPACT` with a recommended safe procedural response.

---

### Step 5: ActionPath & Consultation Preparation (1:35 - 1:50)
1. Navigate to **ActionPath** (`/actionpath`).
2. Review the structured procedural checklist:
   - Step 1: Preserve original offer letter and email threads.
   - Step 2: Issue formal written reply citing Clause 8.2.
   - Step 3: Schedule legal consultation.
3. Review the **"Questions to Ask Qualified Legal Counsel"** section—tailored consultation questions citing exact clauses.
4. Click **Export / Print ActionPath** to verify the clean printable consultation brief.

---

### Step 6: DPDP Privacy & Session Wipe (1:50 - 2:00)
1. Navigate to **Privacy & Security** (`/privacy-security`).
2. In the **Interactive PII Sanitization Simulator**, observe how Aadhaar numbers (`5432 1098 7654`) and PAN cards (`ABCPV1234F`) are masked in real time.
3. Click **Purge All Session Data Now**. Confirm that in-memory buffers and cached documents are permanently destroyed.
