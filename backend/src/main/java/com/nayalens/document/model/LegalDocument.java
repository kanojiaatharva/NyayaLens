package com.nayalens.document.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record LegalDocument(
    String id,
    String filename,
    String fileType,
    long fileSize,
    Instant uploadedAt,
    int pageCount,
    String rawText,
    String sanitizedText,
    List<DocumentPage> pages,
    List<DocumentSection> sections,
    Map<String, Integer> piiSummary,
    boolean piiRedacted
) {
    public LegalDocument {
        if (pages == null) pages = List.of();
        if (sections == null) sections = List.of();
        if (piiSummary == null) piiSummary = Map.of();
    }
}
