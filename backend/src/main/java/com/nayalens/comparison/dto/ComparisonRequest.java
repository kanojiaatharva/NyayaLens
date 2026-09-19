package com.nayalens.comparison.dto;

import jakarta.validation.constraints.NotBlank;

public record ComparisonRequest(
    @NotBlank(message = "Document A ID is required")
    String documentAId,

    @NotBlank(message = "Document B ID is required")
    String documentBId
) {}
