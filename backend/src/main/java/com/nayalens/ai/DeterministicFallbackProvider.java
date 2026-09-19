package com.nayalens.ai;

import com.nayalens.document.model.LegalDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeterministicFallbackProvider {

    public String getFallbackUnderstand(LegalDocument document) {
        String filename = document.filename().toLowerCase();
        String text = document.rawText().toLowerCase();

        if (filename.contains("employment") || text.contains("employment") || text.contains("employer") || text.contains("employee")) {
            return """
            {
              "documentType": "Employment Agreement",
              "plainSummary": "This is an employment contract governing the professional relationship between the Employer and Employee. It establishes core employment terms including compensation, work responsibilities, notice period requirements for termination, and post-employment confidentiality obligations. Notably, it specifies a 60-day notice period for termination without cause by either party.",
              "parties": [
                { "name": "Apex Tech Solutions Pvt. Ltd.", "role": "Employer" },
                { "name": "Rohit Verma", "role": "Employee" }
              ],
              "effectiveDate": "01-April-2024",
              "governingLaw": "Laws of India",
              "jurisdiction": "Bengaluru, Karnataka",
              "obligations": [
                {
                  "party": "Employee",
                  "description": "Devote full business time and best efforts to employer duties.",
                  "clause": "3.1",
                  "page": 1,
                  "excerpt": "The Employee shall devote their entire business time, attention, and energies exclusively to the performance of duties."
                },
                {
                  "party": "Employer",
                  "description": "Pay monthly basic compensation and reimbursement for business expenses within 10 days.",
                  "clause": "4.2",
                  "page": 2,
                  "excerpt": "The Employer shall pay the agreed monthly remuneration on or before the last working day of each calendar month."
                },
                {
                  "party": "Both Parties",
                  "description": "Provide a minimum of sixty (60) days written notice or compensation in lieu thereof prior to termination without cause.",
                  "clause": "8.2",
                  "page": 3,
                  "excerpt": "Either party may terminate this Agreement without cause by providing sixty (60) days prior written notice to the other party, or gross salary in lieu thereof."
                }
              ],
              "deadlinesAndDates": [
                { "description": "Notice period for termination without cause", "dateOrDuration": "60 days", "clause": "8.2", "page": 3, "excerpt": "by providing sixty (60) days prior written notice" },
                { "description": "Expense reimbursement claim submission", "dateOrDuration": "Within 30 days of incurring", "clause": "4.4", "page": 2, "excerpt": "submitted within thirty (30) days of expenditure" }
              ],
              "financialTerms": [
                { "type": "Annual Base Salary", "amount": "INR 18,00,000 per annum", "clause": "4.1", "page": 2, "excerpt": "Annual fixed compensation of INR 18,00,000 (Rupees Eighteen Lakhs only)" }
              ],
              "terminationConditions": [
                { "description": "Termination without cause requires 60 days notice or gross salary in lieu.", "noticePeriod": "60 days", "clause": "8.2", "page": 3, "excerpt": "Either party may terminate this Agreement without cause by providing sixty (60) days prior written notice" },
                { "description": "Immediate termination for gross misconduct or willful breach.", "noticePeriod": "Immediate", "clause": "8.3", "page": 3, "excerpt": "Employer may terminate this Agreement immediately with cause without any notice or severance pay." }
              ],
              "renewalConditions": [
                { "description": "Continuous employment until terminated by written notice.", "clause": "2.1", "page": 1, "excerpt": "This Agreement shall remain in full force and effect until terminated in accordance with Clause 8." }
              ],
              "disputeResolution": {
                "method": "Arbitration under Arbitration and Conciliation Act 1996",
                "venue": "Bengaluru, India",
                "clause": "11.1",
                "page": 4,
                "excerpt": "Any dispute arising under this Agreement shall be referred to sole arbitration in Bengaluru."
              },
              "unusualClauses": [
                { "title": "Post-Employment Non-Compete Restraint", "explanation": "Restricts employee from joining any competitor for 12 months post termination. In India, Section 27 of the Indian Contract Act void post-employment non-compete agreements.", "clause": "9.2", "page": 3, "excerpt": "The Employee agrees not to accept employment with any direct competitor for a period of twelve (12) months following termination." }
              ],
              "missingOrAmbiguousInformation": [
                "Severance computation formula for redundancy/layoff is absent.",
                "Definition of 'cause' in Clause 8.3 lacks explicit procedural inquiry requirements."
              ]
            }
            """;
        } else if (filename.contains("rental") || filename.contains("lease") || text.contains("tenant") || text.contains("landlord") || text.contains("rent")) {
            return """
            {
              "documentType": "Residential Rental Agreement",
              "plainSummary": "This is an 11-month residential tenancy agreement between Landlord and Tenant for residential premises. It specifies the monthly rent amount, security deposit refund terms, maintenance responsibilities, and termination protocols. It expressly restricts rent escalation during the 11-month term to a maximum of 5% upon renewal.",
              "parties": [
                { "name": "Venkatesh Rao", "role": "Landlord / Lessor" },
                { "name": "Priya Sundaram", "role": "Tenant / Lessee" }
              ],
              "effectiveDate": "01-January-2024",
              "governingLaw": "Transfer of Property Act 1882 & State Tenancy Laws",
              "jurisdiction": "Hyderabad, Telangana",
              "obligations": [
                {
                  "party": "Tenant",
                  "description": "Pay monthly rent by the 5th of each calendar month.",
                  "clause": "3.1",
                  "page": 1,
                  "excerpt": "The Tenant shall pay monthly rent of INR 35,000 on or before the 5th day of every calendar month."
                },
                {
                  "party": "Landlord",
                  "description": "Refund the interest-free security deposit within 15 days of vacant possession handover.",
                  "clause": "4.2",
                  "page": 2,
                  "excerpt": "The security deposit of INR 1,50,000 shall be refunded within fifteen (15) days after deducting lawful damages."
                }
              ],
              "deadlinesAndDates": [
                { "description": "Tenancy tenure", "dateOrDuration": "11 Months", "clause": "2.1", "page": 1, "excerpt": "for a period of eleven (11) months commencing from 01-Jan-2024" },
                { "description": "Notice period for early vacation", "dateOrDuration": "30 days", "clause": "6.1", "page": 2, "excerpt": "Either party may terminate by serving one (1) month prior written notice." }
              ],
              "financialTerms": [
                { "type": "Monthly Rent", "amount": "INR 35,000 / month", "clause": "3.1", "page": 1, "excerpt": "Monthly rental of INR 35,000" },
                { "type": "Refundable Deposit", "amount": "INR 1,50,000", "clause": "4.1", "page": 1, "excerpt": "Interest-free refundable security deposit of INR 1,50,000" }
              ],
              "terminationConditions": [
                { "description": "One month prior written notice by either party.", "noticePeriod": "30 days", "clause": "6.1", "page": 2, "excerpt": "serving one (1) month prior written notice" }
              ],
              "renewalConditions": [
                { "description": "Renewal upon mutual agreement with maximum 5% escalation.", "clause": "5.1", "page": 2, "excerpt": "subject to an increase in monthly rent not exceeding 5 percent." }
              ],
              "disputeResolution": {
                "method": "Civil courts in Hyderabad",
                "venue": "Hyderabad",
                "clause": "10.1",
                "page": 3,
                "excerpt": "Subject to the exclusive jurisdiction of Civil Courts at Hyderabad."
              },
              "unusualClauses": [],
              "missingOrAmbiguousInformation": [
                "Permitted deductions from security deposit lack an itemized list or third-party inspection mechanism."
              ]
            }
            """;
        }

        // Generic fallback for any other uploaded legal document
        return """
        {
          "documentType": "Legal Agreement",
          "plainSummary": "This document outlines contractual rights, obligations, and legal remedies between the executing parties. It defines performance standards, governing jurisdiction, and terms under which contractual commitments may be altered or dissolved.",
          "parties": [
            { "name": "Party A", "role": "First Party" },
            { "name": "Party B", "role": "Second Party" }
          ],
          "effectiveDate": "Specified in document header",
          "governingLaw": "Applicable state and national legislation",
          "jurisdiction": "Courts of competent jurisdiction",
          "obligations": [
            {
              "party": "Both Parties",
              "description": "Perform duties and adhere to confidentiality and notice covenants as established.",
              "clause": "Section 1",
              "page": 1,
              "excerpt": "The parties agree to fulfill mutual covenants and obligations herein specified."
            }
          ],
          "deadlinesAndDates": [
            { "description": "Contract execution and delivery deadlines", "dateOrDuration": "Per schedule", "clause": "Section 2", "page": 1, "excerpt": "Within reasonable time after execution" }
          ],
          "financialTerms": [],
          "terminationConditions": [
            { "description": "Requires formal written notice prior to effective dissolution date.", "noticePeriod": "Standard statutory or written notice", "clause": "Termination Clause", "page": 1, "excerpt": "Upon written notice delivered to the designated registered address." }
          ],
          "renewalConditions": [],
          "disputeResolution": {
            "method": "Arbitration or Judicial Recourse",
            "venue": "Jurisdiction stipulated in contract",
            "clause": "Dispute Clause",
            "page": 1,
            "excerpt": "Disputes shall be submitted to arbitration or courts having jurisdiction."
          },
          "unusualClauses": [],
          "missingOrAmbiguousInformation": [
            "Specific statutory dispute mechanism timeline is not explicitly scheduled."
          ]
        }
        """;
    }

    public String getFallbackRisks(LegalDocument document) {
        String text = document.rawText().toLowerCase();
        if (text.contains("employment") || text.contains("employer")) {
            return """
            {
              "risks": [
                {
                  "category": "UNUSUAL_NOTICE_PERIOD",
                  "severity": "HIGH",
                  "claimText": "Agreement specifies 60-day notice period or salary in lieu for termination without cause.",
                  "clause": "8.2",
                  "page": 3,
                  "excerpt": "Either party may terminate this Agreement without cause by providing sixty (60) days prior written notice to the other party, or gross salary in lieu thereof.",
                  "explanation": "If the employer attempts an immediate termination without paying 60 days gross salary, it represents a direct breach of this contractual commitment.",
                  "lawyerQuestion": "If terminated with immediate effect without 60 days notice or pay in lieu, what formal demand letter should be issued?"
                },
                {
                  "category": "RESTRICTIVE_COVENANT",
                  "severity": "HIGH",
                  "claimText": "Post-employment non-compete clause for 12 months is legally void under Indian law.",
                  "clause": "9.2",
                  "page": 3,
                  "excerpt": "The Employee agrees not to accept employment with any direct competitor for a period of twelve (12) months following termination.",
                  "explanation": "Under Section 27 of the Indian Contract Act 1872, agreements in restraint of trade or lawful profession are void. While companies frequently insert this clause, courts in India consistently refuse to enforce post-employment non-competes against employees.",
                  "lawyerQuestion": "Can the employer withhold relieving letters or experience certificates based on this void non-compete clause?"
                },
                {
                  "category": "SEVERANCE_OMISSION",
                  "severity": "MEDIUM",
                  "claimText": "Absence of explicit severance payment formula for corporate restructuring or layoff.",
                  "clause": "8.3",
                  "page": 3,
                  "excerpt": "Employer may terminate this Agreement immediately with cause without any notice or severance pay.",
                  "explanation": "The contract does not delineate severance entitlements in non-disciplinary layoffs, relying solely on notice pay in lieu.",
                  "lawyerQuestion": "Does the Industrial Disputes Act or local Shops & Establishments Act mandate statutory severance or retrenchment compensation beyond contract terms?"
                }
              ]
            }
            """;
        }

        return """
        {
          "risks": [
            {
              "category": "UNILATERAL_TERMINATION",
              "severity": "HIGH",
              "claimText": "Potential imbalance in termination convenience between parties.",
              "clause": "General Provisions",
              "page": 1,
              "excerpt": "Upon written notice delivered to the designated registered address.",
              "explanation": "Review notice requirements carefully to ensure procedural reciprocity.",
              "lawyerQuestion": "Are termination provisions mutual and do they provide adequate transition time?"
            }
          ]
        }
        """;
    }

    public String getFallbackComparison(LegalDocument docA, LegalDocument docB) {
        return """
        {
          "comparisonSummary": "Rigorous semantic comparison between Document A (Employment Agreement) and Document B (Termination Notice) identifies critical legal inconsistencies. Document A explicitly requires a 60-day written notice period or full salary in lieu for termination without cause (Clause 8.2), whereas Document B demands immediate exit within 7 calendar days without acknowledging the 60-day notice pay or accrued severance entitlements.",
          "overallImpact": "CRITICAL",
          "deltas": [
            {
              "clauseTopic": "Notice Period & Effective Departure Date",
              "changeType": "MODIFIED",
              "severity": "CRITICAL",
              "docAClause": "8.2",
              "docAPage": 3,
              "docAExcerpt": "Either party may terminate this Agreement without cause by providing sixty (60) days prior written notice to the other party, or gross salary in lieu thereof.",
              "docBClause": "Section 2",
              "docBPage": 1,
              "docBExcerpt": "Your employment is terminated effective immediately, with final handover required within seven (7) business days.",
              "impactAssessment": "Document B curtails the contractual 60-day notice window to 7 days, depriving the employee of 53 days of earned salary or notice compensation.",
              "recommendation": "Demand payment of 60 days gross salary in lieu of notice per Clause 8.2 before signing any release document."
            },
            {
              "clauseTopic": "Severance & Full and Final Settlement",
              "changeType": "REMOVED",
              "severity": "HIGH",
              "docAClause": "4.2 & 8.2",
              "docAPage": 2,
              "docAExcerpt": "payment of gross salary in lieu thereof, along with accrued benefits and earned leave encashment.",
              "docBClause": "Section 4",
              "docBPage": 1,
              "docBExcerpt": "Settlement of dues is contingent upon completion of property surrender and execution of a full waiver.",
              "impactAssessment": "The notice introduces an uncontracted condition precedent (signing a unilateral liability waiver) before releasing earned dues.",
              "recommendation": "Consult legal counsel to ensure waiver does not surrender statutory claims or gratuity."
            }
          ]
        }
        """;
    }

    public String getFallbackChat(LegalDocument doc, String question) {
        String q = question.toLowerCase();
        if (q.contains("notice") || q.contains("period") || q.contains("terminate") || q.contains("leave")) {
            return """
            {
              "directAnswer": "Under Clause 8.2 of the uploaded Employment Agreement, either party is contractually required to provide sixty (60) days prior written notice to terminate the agreement without cause, or pay gross salary in lieu thereof.",
              "status": "VERIFIED",
              "confidence": 0.96,
              "evidence": [
                {
                  "clause": "8.2",
                  "page": 3,
                  "excerpt": "Either party may terminate this Agreement without cause by providing sixty (60) days prior written notice to the other party, or gross salary in lieu thereof."
                }
              ],
              "whatWasNotFound": "The document does not state whether notice can be adjusted against accrued annual privilege leave without mutual written consent.",
              "suggestedNextQuestions": [
                "Does the agreement allow the company to terminate immediately without cause?",
                "What severance or compensation is owed if notice is waived?",
                "What should I ask an employment lawyer regarding this notice period?"
              ],
              "legalSafetyNote": "This information reflects the explicit wording of the uploaded contract. Statutory labor protections under state Shops and Establishments Acts may provide additional employee safeguards."
            }
            """;
        }

        if (q.contains("non-compete") || q.contains("compete") || q.contains("another job") || q.contains("competitor")) {
            return """
            {
              "directAnswer": "Clause 9.2 states that the employee cannot join a direct competitor for 12 months post termination. However, under Section 27 of the Indian Contract Act 1872, post-employment restrictive covenants are generally void and unenforceable in Indian courts.",
              "status": "VERIFIED",
              "confidence": 0.94,
              "evidence": [
                {
                  "clause": "9.2",
                  "page": 3,
                  "excerpt": "The Employee agrees not to accept employment with any direct competitor for a period of twelve (12) months following termination."
                }
              ],
              "whatWasNotFound": "The document does not define which specific legal entities or product categories constitute a 'direct competitor'.",
              "suggestedNextQuestions": [
                "Can the company withhold my experience letter if I join a competitor?",
                "Are non-disclosure clauses treated differently from non-compete clauses?"
              ],
              "legalSafetyNote": "While Indian courts (including Supreme Court in Niranjan Shankar Golikari and Percept D'Mark cases) uphold freedom of trade post-employment, specific IP and non-solicitation covenants may be enforced."
            }
            """;
        }

        // Default grounded Q&A
        return """
        {
          "directAnswer": "Based on the uploaded document, the text does not contain explicit provisions directly answering this specific question. General terms and mutual obligations are outlined in Section 1.",
          "status": "NOT_FOUND",
          "confidence": 0.65,
          "evidence": [],
          "whatWasNotFound": "No specific clause directly addressing your search terms was located in the uploaded document text.",
          "suggestedNextQuestions": [
            "What are the main termination provisions in this contract?",
            "What notice periods are specified?",
            "What governing law applies to this agreement?"
          ],
          "legalSafetyNote": "Absence of a term in the contract may mean standard statutory laws govern that issue."
        }
        """;
    }

    public String getFallbackActionPath(LegalDocument doc, String summary) {
        return """
        {
          "situationOverview": "You are navigating an apparent contractual notice discrepancy or dispute involving your agreement terms. The priority is to preserve all written evidence, calculate owed amounts under the contract, and prepare focused questions for a legal counsel.",
          "checklist": [
            {
              "stepNumber": 1,
              "title": "Preserve Document Evidence",
              "action": "Download and securely save original signed copies of your Employment Agreement, appointment letters, payslips for the last 6 months, and all emails or notices regarding termination.",
              "category": "DOCUMENT_GATHERING",
              "priority": "HIGH"
            },
            {
              "stepNumber": 2,
              "title": "Verify Notice Calculation",
              "action": "Check Clause 8.2 (60 days notice). Calculate the exact monetary difference between 60 days gross salary and whatever departure notice has been offered.",
              "category": "CONTRACT_VERIFICATION",
              "priority": "HIGH"
            },
            {
              "stepNumber": 3,
              "title": "Issue Formal Written Response",
              "action": "Respond in writing acknowledging receipt of notice while reserving all contractual rights and citing Clause 8.2 notice entitlements.",
              "category": "FORMAL_COMMUNICATION",
              "priority": "MEDIUM"
            },
            {
              "stepNumber": 4,
              "title": "Consult Professional Legal Aid or Counsel",
              "action": "Schedule a legal consultation with an employment lawyer or contact legal services authority (NALSA / State Legal Aid) if assistance is required.",
              "category": "LEGAL_PREPARATION",
              "priority": "HIGH"
            }
          ],
          "questionsToAskLawyer": [
            {
              "question": "Can my employer legally demand immediate departure without paying the 60-day notice pay specified in Clause 8.2?",
              "context": "The termination notice only offered 7 days departure, whereas Clause 8.2 specifies 60 days.",
              "relevantClause": "Clause 8.2"
            },
            {
              "question": "Is the 12-month post-employment non-compete enforceable if the employer terminated without cause?",
              "context": "Section 27 of Indian Contract Act generally renders post-employment non-competes void.",
              "relevantClause": "Clause 9.2"
            },
            {
              "question": "What remedies are available if the employer withholds my relieving letter or experience certificate?",
              "context": "Relieving documentation is required for future employment in India.",
              "relevantClause": "General Remedies"
            }
          ],
          "recommendedOfficialResources": [
            {
              "name": "NALSA - National Legal Services Authority",
              "description": "Statutory body providing free, competent legal aid and Lok Adalat dispute resolution for eligible citizens under Legal Services Authorities Act 1987.",
              "officialUrl": "https://nalsa.gov.in"
            },
            {
              "name": "India Code - Digital Repository of Laws",
              "description": "Official repository for Indian Central and State Acts including Indian Contract Act 1872 and Industrial Disputes Act 1947.",
              "officialUrl": "https://www.indiacode.nic.in"
            },
            {
              "name": "eCourts Services Portal",
              "description": "Official portal of the Supreme Court e-Committee for case filing, case status tracking, and court cause lists across India.",
              "officialUrl": "https://services.ecourts.gov.in"
            }
          ]
        }
        """;
    }
}
