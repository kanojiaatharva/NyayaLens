package com.nayalens.analysis.rules;

import com.nayalens.document.model.DocumentPage;
import com.nayalens.document.model.DocumentSection;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.evidence.model.Claim;
import com.nayalens.evidence.model.Severity;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DeterministicRiskEngine {

    private final EvidenceVerificationService verificationService;

    public DeterministicRiskEngine(EvidenceVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public List<Claim> scanRisks(LegalDocument document) {
        List<Claim> claims = new ArrayList<>();

        // 1. Unilateral Termination Rule
        checkPattern(
                document,
                claims,
                "UNILATERAL_TERMINATION",
                Severity.HIGH,
                Pattern.compile("(?i)(terminate\\s+(?:this\\s+agreement|immediately)\\s+at\\s+(?:its\\s+)?sole\\s+discretion|without\\s+cause\\s+and\\s+without\\s+notice)"),
                "Potentially unfavorable: The agreement appears to grant one party the right to terminate unilaterally without cause or mutual notice.",
                "Does this unilateral termination right align with industry standard norms and applicable statutory notice protections?"
        );

        // 2. Short Notice Period / Abrupt Departure Rule (< 30 days)
        checkPattern(
                document,
                claims,
                "UNUSUAL_NOTICE_PERIOD",
                Severity.HIGH,
                Pattern.compile("(?i)((?:\\b(?:7|10|14|15)\\b|seven|ten|fourteen|fifteen)\\s+(?:calendar\\s+|business\\s+)?days(?:'|\\s+)?(?:written\\s+)?(?:notice|from\\s+the\\s+date|handover|departure|timeline)?)"),
                "Potentially important: The specified notice period or handover window is shorter than standard 30 or 60 day norms, limiting preparation time upon termination.",
                "Is a shortened notice period enforceable under local labor or tenancy laws in this jurisdiction?"
        );

        // 3. Unlimited Liability Rule
        checkPattern(
                document,
                claims,
                "UNLIMITED_LIABILITY",
                Severity.CRITICAL,
                Pattern.compile("(?i)(unlimited\\s+liability|no\\s+cap\\s+on\\s+liability|liable\\s+for\\s+all\\s+(?:consequential|indirect|punitive)\\s+damages)"),
                "Potentially critical: This provision appears to create uncapped financial liability without traditional caps linked to contract fees.",
                "Can liability be reasonably capped to a fixed monetary ceiling (such as fees paid over the previous 12 months)?"
        );

        // 4. Automatic Renewal / Evergreen Lock-in
        checkPattern(
                document,
                claims,
                "AUTOMATIC_RENEWAL",
                Severity.MEDIUM,
                Pattern.compile("(?i)(shall\\s+automatically\\s+renew(?:\\s+for\\s+(?:successive|additional)\\s+periods)?|evergreen\\s+clause|unless\\s+written\\s+notice\\s+is\\s+given\\s+at\\s+least\\s+(?:60|90)\\s+days)"),
                "Potentially important: The contract contains an auto-renewal mechanism that may lock the party into another term unless terminated well in advance.",
                "What is the exact deadline window required to provide notice of non-renewal?"
        );

        // 5. Restrictive Non-Compete
        checkPattern(
                document,
                claims,
                "RESTRICTIVE_NON_COMPETE",
                Severity.HIGH,
                Pattern.compile("(?i)(not\\s+(?:to\\s+)?accept\\s+employment\\s+with\\s+any\\s+(?:direct\\s+)?competitor|shall\\s+not\\s+(?:directly\\s+or\\s+indirectly\\s+)?(?:engage|participate|compete)\\s+in\\s+(?:any\\s+)?competing|non-compete)"),
                "Potentially important: Contains a restrictive covenant against competitive employment. In India (Section 27 of Indian Contract Act 1872), post-employment non-compete agreements are generally void.",
                "Under applicable Indian jurisdiction (Section 27, Indian Contract Act), is this post-termination restraint legally enforceable?"
        );

        // 6. Broad Indemnification
        checkPattern(
                document,
                claims,
                "BROAD_INDEMNIFICATION",
                Severity.MEDIUM,
                Pattern.compile("(?i)(indemnify,\\s+defend\\s+and\\s+hold\\s+harmless[\\s\\S]{1,80}any\\s+and\\s+all\\s+claims)"),
                "Potentially unfavorable: Broad indemnity language requires you to defend the other party against broad third-party liabilities.",
                "Can the indemnification obligation be narrowed strictly to direct damages caused by gross negligence or willful misconduct?"
        );

        // 7. Dispute Resolution / Distant Venue
        checkPattern(
                document,
                claims,
                "JURISDICTION_AND_VENUE",
                Severity.LOW,
                Pattern.compile("(?i)(exclusive\\s+jurisdiction\\s+of\\s+the\\s+courts\\s+(?:of|in)|arbitration\\s+shall\\s+be\\s+conducted\\s+in\\s+([A-Z][a-zA-Z\\s]{3,30}))"),
                "Requires review: Specifies an exclusive jurisdiction or arbitration venue, which could require significant travel or legal expenses to litigate.",
                "Does the designated arbitration or court venue create an impractical barrier to seeking relief?"
        );

        return claims;
    }

    private void checkPattern(
            LegalDocument document,
            List<Claim> claims,
            String category,
            Severity severity,
            Pattern pattern,
            String explanation,
            String lawyerQuestion
    ) {
        for (DocumentSection section : document.sections()) {
            Matcher m = pattern.matcher(section.content());
            if (m.find()) {
                // Find matching sentence for context excerpt
                String matchSnippet = extractSurroundingSentence(section.content(), m.start(), m.end());

                Claim claim = verificationService.verifyClaim(
                        document,
                        "Detected potentially important " + category.toLowerCase().replace('_', ' ') + " provision",
                        category,
                        severity,
                        section.clauseNumber(),
                        section.pageNumber(),
                        matchSnippet,
                        explanation,
                        lawyerQuestion,
                        false
                );
                claims.add(claim);
                return; // One claim per category to prevent duplicate spam
            }
        }

        // Check raw pages if not found in section headers
        for (DocumentPage page : document.pages()) {
            Matcher m = pattern.matcher(page.text());
            if (m.find()) {
                String matchSnippet = extractSurroundingSentence(page.text(), m.start(), m.end());
                Claim claim = verificationService.verifyClaim(
                        document,
                        "Detected potentially important " + category.toLowerCase().replace('_', ' ') + " provision",
                        category,
                        severity,
                        "P" + page.pageNumber(),
                        page.pageNumber(),
                        matchSnippet,
                        explanation,
                        lawyerQuestion,
                        false
                );
                claims.add(claim);
                return;
            }
        }
    }

    private String extractSurroundingSentence(String text, int start, int end) {
        int sentenceStart = Math.max(0, text.lastIndexOf('.', start));
        if (sentenceStart > 0) sentenceStart++; // Skip period

        int sentenceEnd = text.indexOf('.', end);
        if (sentenceEnd == -1) {
            sentenceEnd = Math.min(text.length(), end + 100);
        } else {
            sentenceEnd = Math.min(text.length(), sentenceEnd + 1);
        }

        String snippet = text.substring(sentenceStart, sentenceEnd).trim().replaceAll("\\s+", " ");
        if (snippet.length() > 220) {
            snippet = snippet.substring(0, 220) + "...";
        }
        return snippet;
    }
}
