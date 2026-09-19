package com.nayalens.analysis.model;

import com.nayalens.evidence.model.Claim;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record AnalysisResult(
    String documentId,
    String documentType,
    String plainSummary,
    List<PartyInfo> parties,
    String effectiveDate,
    String governingLaw,
    String jurisdiction,
    List<ObligationItem> obligations,
    List<DeadlineItem> deadlines,
    List<FinancialTermItem> financialTerms,
    List<TerminationItem> terminationConditions,
    List<RenewalItem> renewalConditions,
    DisputeResolutionItem disputeResolution,
    List<UnusualClauseItem> unusualClauses,
    List<String> missingOrAmbiguousInformation,
    List<Claim> risks,
    Instant analyzedAt,
    String modelUsed
) {
    public record PartyInfo(String name, String role) {}
    public record ObligationItem(String party, String description, String clause, int page, String excerpt) {}
    public record DeadlineItem(String description, String dateOrDuration, String clause, int page, String excerpt) {}
    public record FinancialTermItem(String type, String amount, String clause, int page, String excerpt) {}
    public record TerminationItem(String description, String noticePeriod, String clause, int page, String excerpt) {}
    public record RenewalItem(String description, String clause, int page, String excerpt) {}
    public record DisputeResolutionItem(String method, String venue, String clause, int page, String excerpt) {}
    public record UnusualClauseItem(String title, String explanation, String clause, int page, String excerpt) {}
}
