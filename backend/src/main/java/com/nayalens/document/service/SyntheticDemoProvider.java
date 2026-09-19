package com.nayalens.document.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class SyntheticDemoProvider {

    public byte[] getDemoFileBytes(String scenario) {
        String content = switch (scenario.toLowerCase()) {
            case "employment-agreement" -> EMPLOYMENT_AGREEMENT;
            case "termination-notice" -> TERMINATION_NOTICE;
            case "rental-agreement" -> RENTAL_AGREEMENT;
            case "rent-escalation-notice", "rent-increase-notice" -> RENT_ESCALATION_NOTICE;
            case "mutual-nda" -> MUTUAL_NDA;
            case "vendor-nda", "unilateral-vendor-nda" -> VENDOR_NDA;
            default -> EMPLOYMENT_AGREEMENT;
        };
        return content.getBytes(StandardCharsets.UTF_8);
    }

    public String getDemoFilename(String scenario) {
        return switch (scenario.toLowerCase()) {
            case "employment-agreement" -> "Apex_Employment_Agreement.txt";
            case "termination-notice" -> "Apex_Termination_Notice.txt";
            case "rental-agreement" -> "Hyd_Rental_Agreement_2024.txt";
            case "rent-escalation-notice", "rent-increase-notice" -> "Landlord_Escalation_Notice.txt";
            case "mutual-nda" -> "Mutual_Non_Disclosure_Agreement.txt";
            case "vendor-nda", "unilateral-vendor-nda" -> "Vendor_Unilateral_NDA.txt";
            default -> "Sample_Agreement.txt";
        };
    }

    public static final String EMPLOYMENT_AGREEMENT = """
            EMPLOYMENT AGREEMENT

            This Employment Agreement (the "Agreement") is executed on 01-April-2024 at Bengaluru, India by and between:

            1. Apex Tech Solutions Private Limited, a company incorporated under the Companies Act 2013, having its registered office at Outer Ring Road, Bellandur, Bengaluru, Karnataka 560103 (hereinafter referred to as "Employer"); and
            2. Mr. Rohit Verma, residing at HSR Layout, Sector 2, Bengaluru, Karnataka 560102, holding Aadhaar 4521-8892-1044 and PAN ABCPV1234F (hereinafter referred to as "Employee").

            NOW THEREFORE, in consideration of mutual covenants, the parties agree as follows:

            Clause 1. Position and Scope of Work
            The Employee is appointed as Principal Software Architect. The Employee shall report directly to the Chief Technology Officer.

            Clause 2. Term and Duration
            This Agreement shall remain in full force and effect until terminated in accordance with Clause 8 herein.

            Clause 3. Employee Obligations
            3.1 The Employee shall devote their entire business time, attention, and energies exclusively to the performance of duties for the Employer.
            3.2 The Employee shall adhere to all professional codes of conduct, data safety policies, and statutory compliance norms.

            Clause 4. Remuneration and Financial Covenants
            4.1 Annual fixed compensation of INR 18,00,000 (Rupees Eighteen Lakhs only) payable monthly in twelve equal installments.
            4.2 The Employer shall pay the agreed monthly remuneration on or before the last working day of each calendar month.
            4.3 Provident fund and statutory deductions shall be made as mandated by applicable labor regulations.
            4.4 Legitimate business expenses incurred shall be reimbursed within thirty (30) days of expenditure submission.

            Clause 5. Confidential Information
            The Employee acknowledges that proprietary algorithms, customer lists, source code, and commercial designs constitute confidential trade secrets. The Employee shall maintain strict secrecy both during and after the tenure.

            Clause 6. Intellectual Property
            All inventions, software routines, documentation, and improvements authored or conceived during employment shall belong exclusively to the Employer as work-for-hire.

            Clause 7. Working Hours and Leave
            Standard working hours are 40 hours per week. The Employee is entitled to 20 days of paid privilege leave per annum.

            Clause 8. Termination of Employment
            8.1 Either party may terminate during probation by serving 15 days written notice.
            8.2 Following confirmation, either party may terminate this Agreement without cause by providing sixty (60) days prior written notice to the other party, or gross salary in lieu thereof, along with accrued benefits and earned leave encashment.
            8.3 The Employer may terminate this Agreement immediately with cause without any notice or severance pay in events of proven financial embezzlement, gross insubordination, or conviction of a criminal offense involving moral turpitude.

            Clause 9. Restrictive Covenants and Non-Compete
            9.1 During the term of employment, the Employee shall not engage in any outside commercial activity.
            9.2 The Employee agrees not to accept employment with any direct competitor for a period of twelve (12) months following termination. (Note: Subject to Section 27, Indian Contract Act 1872).

            Clause 10. Indemnification and Liability
            Each party agrees to indemnify and hold harmless the other against direct third-party damages arising from gross negligence or willful misconduct.

            Clause 11. Dispute Resolution and Governing Law
            11.1 This Agreement is governed by the Laws of India.
            11.2 Any dispute arising under this Agreement shall be referred to sole arbitration in Bengaluru in accordance with the Arbitration and Conciliation Act 1996. The courts at Bengaluru shall have exclusive jurisdiction.

            IN WITNESS WHEREOF, the parties hereto have executed this Agreement.
            """;

    public static final String TERMINATION_NOTICE = """
            TERMINATION OF EMPLOYMENT NOTICE

            Date: 15-September-2024
            To: Mr. Rohit Verma (Emp ID: APX-4091)
            From: Human Resources Department, Apex Tech Solutions Pvt. Ltd., Bengaluru

            Dear Rohit,

            Subject: Notice of Separation from Services

            This letter serves as formal notification that your employment with Apex Tech Solutions Private Limited is being concluded as part of departmental restructuring and operational downsizing.

            Section 1. Reason for Separation
            Due to macroeconomic realignment and sunsetting of enterprise client product lines, your role as Principal Software Architect has become redundant. This decision does not reflect upon your individual performance.

            Section 2. Effective Date and Notice Timeline
            Your employment is terminated effective immediately, with final handover required within seven (7) business days from the date of this notice (i.e. by 24-September-2024).

            Section 3. Property and Asset Handover
            You are required to surrender all company assets, including your Apple MacBook Pro, building security access cards, and any external storage devices, to the IT Administration desk before 24-September-2024.

            Section 4. Final Settlement and Severance Terms
            Final settlement of dues is contingent upon completion of property surrender and execution of a full waiver releasing the company from any future claims. No severance or additional notice compensation will be dispersed beyond standard salary for days worked up to 24-September-2024.

            Section 5. Post-Separation Reminders
            Please be advised that confidentiality covenants and Clause 9 non-compete obligations survive your separation from the company.

            Sincerely,
            Head of People Operations
            Apex Tech Solutions Pvt. Ltd.
            """;

    public static final String RENTAL_AGREEMENT = """
            RESIDENTIAL TENANCY LEASE AGREEMENT

            This Agreement is made on 01-January-2024 at Hyderabad, Telangana between:
            1. Sri Venkatesh Rao, residing at Banjara Hills, Road No. 12, Hyderabad (hereinafter "Landlord"); and
            2. Ms. Priya Sundaram, residing at Flat 402, Green Meadows, Madhapur, Hyderabad (hereinafter "Tenant").

            Clause 1. Premises
            Flat No. 402, Green Meadows Apartments, Madhapur, Hyderabad, comprising 3 Bedrooms, Hall, and Kitchen.

            Clause 2. Duration
            The lease is for an agreed period of eleven (11) months commencing from 01-Jan-2024 to 30-Nov-2024.

            Clause 3. Rent and Utility Dues
            3.1 The Tenant shall pay monthly rental of INR 35,000 on or before the 5th day of every calendar month.
            3.2 Electricity and water consumption charges shall be paid directly by the Tenant according to meter readings.

            Clause 4. Security Deposit
            4.1 Interest-free refundable security deposit of INR 1,50,000 paid via bank transfer.
            4.2 The security deposit shall be refunded within fifteen (15) days after vacating and handing over vacant possession, after deducting lawful repair charges.

            Clause 5. Escalation on Renewal
            5.1 If both parties agree in writing to extend the tenancy beyond 11 months, the monthly rent may be revised subject to an increase not exceeding 5 percent.

            Clause 6. Termination and Vacating
            6.1 Either party may terminate the lease by serving one (1) month prior written notice.
            6.2 Immediate eviction permitted only upon non-payment of rent for consecutive two months.

            Clause 7. Governing Law
            Subject to the exclusive jurisdiction of Civil Courts at Hyderabad.
            """;

    public static final String RENT_ESCALATION_NOTICE = """
            NOTICE FOR REVISION OF RENT

            Date: 10-October-2024
            To: Ms. Priya Sundaram, Flat 402, Green Meadows, Madhapur, Hyderabad
            From: Sri Venkatesh Rao (Landlord)

            Subject: Rent Revision for Prospective Renewal

            Dear Priya,

            As our 11-month lease is concluding in November 2024, please note that due to prevailing high market demand in the Madhapur tech corridor, the revised monthly rent will be INR 44,000 (a 25.7% increase) starting 01-December-2024.

            If this revision is not accepted in writing by 25-October-2024, you are requested to vacate the premises upon lease expiration without further notice.

            Regards,
            Venkatesh Rao
            """;

    public static final String MUTUAL_NDA = """
            MUTUAL NON-DISCLOSURE AGREEMENT

            This Mutual Non-Disclosure Agreement ("Agreement") is entered into by Alpha Innovations Pvt Ltd and Beta Dynamics Ltd.

            Clause 1. Purpose
            Evaluating potential strategic technical partnership and joint venture collaboration.

            Clause 2. Definition of Confidential Information
            All technical, financial, and product design materials marked as "Confidential".

            Clause 3. Term of Confidentiality
            Confidentiality obligations shall endure for a period of two (2) years from disclosure date.

            Clause 4. Permitted Exceptions
            Information already public, independently developed without reference to disclosed data, or required to be disclosed by court order.

            Clause 5. Governing Law
            Laws of India, courts of New Delhi have jurisdiction.
            """;

    public static final String VENDOR_NDA = """
            UNILATERAL VENDOR PROPRIETARY NON-DISCLOSURE AGREEMENT

            Clause 1. Unilateral Secrecy
            Vendor agrees to maintain all Client information in perpetual secrecy without expiration.

            Clause 2. Indemnity and Unlimited Damages
            Vendor agrees to indemnify Client for any indirect, consequential, or punitive damages arising from inadvertent disclosure, without any monetary liability cap.

            Clause 3. Non-Solicitation and Non-Compete
            Vendor shall not offer services to any entity in the same industry domain for 36 months.

            Clause 4. Exclusive Jurisdiction
            Exclusive jurisdiction of the courts of London, United Kingdom.
            """;
}
