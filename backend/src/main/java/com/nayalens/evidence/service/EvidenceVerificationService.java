package com.nayalens.evidence.service;

import com.nayalens.common.util.IdGenerator;
import com.nayalens.document.model.DocumentPage;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.evidence.model.Claim;
import com.nayalens.evidence.model.Evidence;
import com.nayalens.evidence.model.Severity;
import com.nayalens.evidence.model.VerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvidenceVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EvidenceVerificationService.class);

    /**
     * Verifies and grounds a candidate claim against the source document.
     */
    public Claim verifyClaim(
            LegalDocument document,
            String claimText,
            String category,
            Severity severity,
            String claimedClause,
            int claimedPage,
            String claimedExcerpt,
            String explanation,
            String lawyerQuestion,
            boolean isExplicitInference
    ) {
        if (claimedExcerpt == null || claimedExcerpt.isBlank()) {
            // No evidence provided
            VerificationStatus status = isExplicitInference ? VerificationStatus.INFERRED : VerificationStatus.NEEDS_REVIEW;
            return new Claim(
                    IdGenerator.claimId(),
                    claimText,
                    category,
                    severity,
                    status,
                    0.50,
                    List.of(),
                    explanation,
                    lawyerQuestion
            );
        }

        // Locate page in document
        Optional<DocumentPage> targetPage = document.pages().stream()
                .filter(p -> p.pageNumber() == claimedPage)
                .findFirst();

        double score = 0.0;
        int verifiedPage = claimedPage;

        if (targetPage.isPresent()) {
            score = calculateTextSimilarity(claimedExcerpt, targetPage.get().text());
        }

        // If not found on cited page, scan other pages to detect page misattribution
        if (score < 0.6) {
            for (DocumentPage p : document.pages()) {
                double altScore = calculateTextSimilarity(claimedExcerpt, p.text());
                if (altScore > score) {
                    score = altScore;
                    verifiedPage = p.pageNumber();
                }
            }
        }

        VerificationStatus status;
        double confidence;

        if (isExplicitInference) {
            status = VerificationStatus.INFERRED;
            confidence = Math.min(0.75, Math.max(0.50, score));
        } else if (score >= 0.80) {
            status = VerificationStatus.VERIFIED;
            confidence = Math.min(0.98, Math.max(0.85, score));
        } else if (score >= 0.45) {
            status = VerificationStatus.NEEDS_REVIEW;
            confidence = Math.min(0.79, Math.max(0.55, score));
        } else {
            status = VerificationStatus.NEEDS_REVIEW;
            confidence = 0.40;
            explanation = explanation + " [Notice: The cited excerpt could not be directly confirmed in the uploaded document text.]";
        }

        Evidence evidence = new Evidence(
                IdGenerator.evidenceId(),
                document.id(),
                document.filename(),
                verifiedPage,
                "Section " + (claimedClause != null ? claimedClause : "General"),
                claimedClause != null ? claimedClause : "N/A",
                claimedExcerpt.trim(),
                "USER_DOCUMENT",
                confidence
        );

        return new Claim(
                IdGenerator.claimId(),
                claimText,
                category,
                severity,
                status,
                confidence,
                List.of(evidence),
                explanation,
                lawyerQuestion
        );
    }

    /**
     * Compute token-level Jaccard & containment similarity between excerpt and candidate page text.
     */
    public double calculateTextSimilarity(String excerpt, String fullText) {
        if (excerpt == null || fullText == null || excerpt.isBlank() || fullText.isBlank()) {
            return 0.0;
        }

        String normExcerpt = normalize(excerpt);
        String normFull = normalize(fullText);

        // Exact substring containment is 100% verified
        if (normFull.contains(normExcerpt)) {
            return 1.0;
        }

        // Otherwise evaluate token sets
        Set<String> excerptTokens = tokenize(normExcerpt);
        Set<String> fullTokens = tokenize(normFull);

        if (excerptTokens.isEmpty()) return 0.0;

        int matchCount = 0;
        for (String token : excerptTokens) {
            if (fullTokens.contains(token)) {
                matchCount++;
            }
        }

        // Proportion of excerpt tokens found in target text
        return (double) matchCount / excerptTokens.size();
    }

    private String normalize(String s) {
        return s.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Set<String> tokenize(String s) {
        String[] words = s.split("\\s+");
        Set<String> set = new HashSet<>();
        for (String w : words) {
            if (w.length() > 2) { // Skip 1-2 char stop tokens
                set.add(w);
            }
        }
        return set;
    }
}
