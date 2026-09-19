package com.nayalens.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatQuestionRequest(
    @NotBlank(message = "Document ID is required")
    String documentId,

    @NotBlank(message = "Question cannot be empty")
    @Size(max = 1000, message = "Question cannot exceed 1000 characters")
    String question
) {}
