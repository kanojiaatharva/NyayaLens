package com.nayalens.legalresources.model;

public record LegalResource(
    String id,
    String name,
    String category, // "STATUTORY_REPOSITORY", "JUDICIAL_PORTAL", "LEGAL_AID", "REGULATORY"
    String description,
    String officialUrl,
    String authorityLevel, // "OFFICIAL_GOVERNMENT", "CONSTITUTIONAL_BODY", "STATUTORY_AUTHORITY"
    String keyLegislationCovered
) {}
