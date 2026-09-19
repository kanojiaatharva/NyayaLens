package com.nayalens.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.ai.GeminiClient;
import com.nayalens.ai.PromptRegistry;
import com.nayalens.chat.dto.ChatAnswerResponse;
import com.nayalens.chat.dto.ChatQuestionRequest;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.common.util.IdGenerator;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.evidence.model.Evidence;
import com.nayalens.evidence.model.VerificationStatus;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChatService {

    private static final Logger log = LoggerFactory.getLogger(DocumentChatService.class);

    private final DocumentRepository documentRepository;
    private final GeminiClient geminiClient;
    private final EvidenceVerificationService verificationService;
    private final ObjectMapper objectMapper;

    public DocumentChatService(
            DocumentRepository documentRepository,
            GeminiClient geminiClient,
            EvidenceVerificationService verificationService,
            ObjectMapper objectMapper
    ) {
        this.documentRepository = documentRepository;
        this.geminiClient = geminiClient;
        this.verificationService = verificationService;
        this.objectMapper = objectMapper;
    }

    public ChatAnswerResponse answerQuestion(ChatQuestionRequest request) {
        LegalDocument document = documentRepository.findById(request.documentId())
                .orElseThrow(() -> new DocumentNotFoundException(request.documentId()));

        log.info("Processing document Q&A for doc [{}] question: '{}'", document.filename(), request.question());

        String prompt = PromptRegistry.buildChatPrompt(document.sanitizedText(), request.question());
        String aiJson = geminiClient.generateStructuredJson(prompt, GeminiClient.ModelTier.FLASH);
        String modelUsed = "gemini-3.8-flash";

        if (aiJson == null) {
            aiJson = geminiClient.getFallbackProvider().getFallbackChat(document, request.question());
            modelUsed = "deterministic-qa-fallback";
        }

        return parseChatAnswer(document, aiJson, modelUsed);
    }

    private ChatAnswerResponse parseChatAnswer(LegalDocument document, String rawJson, String modelUsed) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            String directAnswer = root.path("directAnswer").asText("No direct answer could be formulated from the document.");
            VerificationStatus rawStatus = VerificationStatus.valueOf(root.path("status").asText("INFERRED").toUpperCase());
            double confidence = root.path("confidence").asDouble(0.85);
            String whatWasNotFound = root.path("whatWasNotFound").asText("None noted.");
            String legalSafetyNote = root.path("legalSafetyNote").asText("This response provides document-grounded legal information and is not formal legal advice.");

            List<Evidence> evidenceList = new ArrayList<>();
            JsonNode evNode = root.path("evidence");
            if (evNode.isArray()) {
                for (JsonNode e : evNode) {
                    String clause = e.path("clause").asText("General");
                    int page = e.path("page").asInt(1);
                    String excerpt = e.path("excerpt").asText();

                    double similarity = verificationService.calculateTextSimilarity(excerpt, document.rawText());
                    double evConfidence = Math.max(0.50, Math.min(0.99, similarity));

                    evidenceList.add(new Evidence(
                            IdGenerator.evidenceId(),
                            document.id(),
                            document.filename(),
                            page,
                            "Clause " + clause,
                            clause,
                            excerpt,
                            "USER_DOCUMENT",
                            evConfidence
                    ));
                }
            }

            // Hallucination Guard: If no evidence exists and status was VERIFIED, downgrade
            VerificationStatus finalStatus = rawStatus;
            if (evidenceList.isEmpty() && rawStatus == VerificationStatus.VERIFIED) {
                finalStatus = VerificationStatus.INFERRED;
                confidence = Math.min(confidence, 0.70);
            }

            List<String> nextQuestions = new ArrayList<>();
            JsonNode nextQNode = root.path("suggestedNextQuestions");
            if (nextQNode.isArray()) {
                for (JsonNode nq : nextQNode) {
                    nextQuestions.add(nq.asText());
                }
            }

            return new ChatAnswerResponse(
                    directAnswer,
                    finalStatus,
                    confidence,
                    evidenceList,
                    whatWasNotFound,
                    nextQuestions,
                    legalSafetyNote,
                    modelUsed
            );
        } catch (Exception e) {
            log.error("Failed to parse chat response JSON: {}", e.getMessage(), e);
            return new ChatAnswerResponse(
                    "The uploaded document does not contain an explicit provision directly addressing this query.",
                    VerificationStatus.NOT_FOUND,
                    0.50,
                    List.of(),
                    "Specific terms matching the question were not located.",
                    List.of("What are the core obligations in this document?"),
                    "General statutory provisions may govern absent contractual terms.",
                    "fallback-engine"
            );
        }
    }
}
