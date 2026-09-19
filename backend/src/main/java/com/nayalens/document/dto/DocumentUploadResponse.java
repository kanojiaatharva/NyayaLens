package com.nayalens.document.dto;

import java.time.Instant;
import java.util.Map;

public record DocumentUploadResponse(
    String id,
    String filename,
    String fileType,
    long fileSize,
    int pageCount,
    int sectionCount,
    Instant uploadedAt,
    Map<String, Integer> piiSummary,
    boolean piiRedacted
) {}
