package com.nayalens.comparison.model;

import com.nayalens.evidence.model.Severity;

public record ClauseDelta(
    String id,
    String clauseTopic,
    String changeType, // "ADDED", "REMOVED", "MODIFIED", "UNCHANGED"
    Severity severity,
    String docAClause,
    int docAPage,
    String docAExcerpt,
    String docBClause,
    int docBPage,
    String docBExcerpt,
    String impactAssessment,
    String recommendation
) {}
