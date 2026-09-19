package com.nayalens.evidence.model;

import java.util.List;

public record Claim(
    String id,
    String claimText,
    String category,
    Severity severity,
    VerificationStatus status,
    double confidence,
    List<Evidence> evidenceList,
    String explanation,
    String lawyerQuestion
) {
    public Claim {
        if (evidenceList == null) {
            evidenceList = List.of();
        }
    }
}
