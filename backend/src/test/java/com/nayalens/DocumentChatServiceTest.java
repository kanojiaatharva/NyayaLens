package com.nayalens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.ai.DeterministicFallbackProvider;
import com.nayalens.ai.GeminiClient;
import com.nayalens.chat.dto.ChatAnswerResponse;
import com.nayalens.chat.dto.ChatQuestionRequest;
import com.nayalens.chat.service.DocumentChatService;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.config.GeminiProperties;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.document.repository.InMemoryDocumentRepository;
import com.nayalens.document.service.DocumentParserService;
import com.nayalens.document.service.SyntheticDemoProvider;
import com.nayalens.evidence.model.VerificationStatus;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentChatServiceTest {

    private DocumentChatService chatService;
    private DocumentRepository documentRepository;
    private DocumentParserService parserService;
    private SyntheticDemoProvider demoProvider;

    @BeforeEach
    void setUp() {
        documentRepository = new InMemoryDocumentRepository();
        parserService = new DocumentParserService(50);
        demoProvider = new SyntheticDemoProvider();

        ObjectMapper objectMapper = new ObjectMapper();
        GeminiProperties properties = new GeminiProperties("", "gemini-3.8-flash", "gemini-3.1-pro-preview", 30, 2);
        DeterministicFallbackProvider fallbackProvider = new DeterministicFallbackProvider();
        GeminiClient geminiClient = new GeminiClient(properties, objectMapper, fallbackProvider);
        EvidenceVerificationService verificationService = new EvidenceVerificationService();

        chatService = new DocumentChatService(documentRepository, geminiClient, verificationService, objectMapper);
    }

    @Test
    @DisplayName("Should ground notice period question with verified evidence excerpt")
    void testAnswerNoticePeriod() {
        byte[] bytes = demoProvider.getDemoFileBytes("employment-agreement");
        LegalDocument doc = parserService.parseDocument("Employment.txt", bytes, true);
        documentRepository.save(doc);

        ChatQuestionRequest req = new ChatQuestionRequest(doc.id(), "What is the contractual notice period required for termination?");
        ChatAnswerResponse res = chatService.answerQuestion(req);

        assertNotNull(res);
        assertTrue(res.directAnswer().contains("60") || res.directAnswer().contains("sixty"));
        assertEquals(VerificationStatus.VERIFIED, res.status());
        assertTrue(res.confidence() >= 0.80);
        assertFalse(res.evidence().isEmpty());
        assertNotNull(res.whatWasNotFound());
        assertFalse(res.suggestedNextQuestions().isEmpty());
    }

    @Test
    @DisplayName("Should answer non-compete questions citing Section 27 and enforce grounding")
    void testAnswerNonCompete() {
        byte[] bytes = demoProvider.getDemoFileBytes("employment-agreement");
        LegalDocument doc = parserService.parseDocument("Employment.txt", bytes, true);
        documentRepository.save(doc);

        ChatQuestionRequest req = new ChatQuestionRequest(doc.id(), "Can I work for a competitor after leaving?");
        ChatAnswerResponse res = chatService.answerQuestion(req);

        assertNotNull(res);
        assertTrue(res.directAnswer().contains("27") || res.directAnswer().contains("competitor"));
        assertEquals(VerificationStatus.VERIFIED, res.status());
    }

    @Test
    @DisplayName("Should throw DocumentNotFoundException for unknown document ID")
    void testChatUnknownDocument() {
        ChatQuestionRequest req = new ChatQuestionRequest("invalid_doc_id", "What is the notice period?");
        assertThrows(DocumentNotFoundException.class, () -> chatService.answerQuestion(req));
    }
}
