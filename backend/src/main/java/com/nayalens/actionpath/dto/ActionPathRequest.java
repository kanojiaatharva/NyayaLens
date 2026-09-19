package com.nayalens.actionpath.dto;

import jakarta.validation.constraints.NotBlank;

public record ActionPathRequest(
    @NotBlank(message = "Document ID is required")
    String documentId
) {}
