package com.nayalens.evidence.model;

public record Evidence(
    String id,
    String documentId,
    String documentName,
    int pageNumber,
    String section,
    String clause,
    String excerpt,
    String sourceType, // "USER_DOCUMENT", "OFFICIAL_STATUTE", "JUDICIAL_PRECEDENT"
    double confidence
) {}
