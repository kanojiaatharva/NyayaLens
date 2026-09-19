package com.nayalens.document.model;

public record DocumentSection(
    String id,
    String clauseNumber,
    String title,
    int pageNumber,
    String content
) {}
