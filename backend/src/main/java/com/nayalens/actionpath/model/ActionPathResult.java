package com.nayalens.actionpath.model;

import java.time.Instant;
import java.util.List;

public record ActionPathResult(
    String id,
    String documentId,
    String situationOverview,
    List<ActionStep> checklist,
    List<LawyerQuestion> questionsToAskLawyer,
    List<OfficialResourceItem> recommendedOfficialResources,
    Instant generatedAt,
    String modelUsed
) {
    public record ActionStep(
        int stepNumber,
        String title,
        String action,
        String category, // "DOCUMENT_GATHERING", "CONTRACT_VERIFICATION", "LEGAL_PREPARATION", "FORMAL_COMMUNICATION"
        String priority  // "HIGH", "MEDIUM", "LOW"
    ) {}

    public record LawyerQuestion(
        String question,
        String context,
        String relevantClause
    ) {}

    public record OfficialResourceItem(
        String name,
        String description,
        String officialUrl
    ) {}
}
