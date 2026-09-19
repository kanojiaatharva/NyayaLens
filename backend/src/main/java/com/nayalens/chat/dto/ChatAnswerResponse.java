package com.nayalens.chat.dto;

import com.nayalens.evidence.model.Evidence;
import com.nayalens.evidence.model.VerificationStatus;

import java.util.List;

public record ChatAnswerResponse(
    String directAnswer,
    VerificationStatus status,
    double confidence,
    List<Evidence> evidence,
    String whatWasNotFound,
    List<String> suggestedNextQuestions,
    String legalSafetyNote,
    String modelUsed
) {
    public ChatAnswerResponse {
        if (evidence == null) evidence = List.of();
        if (suggestedNextQuestions == null) suggestedNextQuestions = List.of();
    }
}
