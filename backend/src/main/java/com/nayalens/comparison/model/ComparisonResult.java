package com.nayalens.comparison.model;

import com.nayalens.evidence.model.Severity;
import java.time.Instant;
import java.util.List;

public record ComparisonResult(
    String id,
    String documentAId,
    String documentAName,
    String documentBId,
    String documentBName,
    String comparisonSummary,
    Severity overallImpact,
    List<ClauseDelta> deltas,
    Instant comparedAt,
    String modelUsed
) {}
