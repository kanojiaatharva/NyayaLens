package com.nayalens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.actionpath.model.ActionPathResult;
import com.nayalens.actionpath.service.ActionPathService;
import com.nayalens.ai.DeterministicFallbackProvider;
import com.nayalens.ai.GeminiClient;
import com.nayalens.analysis.rules.DeterministicRiskEngine;
import com.nayalens.analysis.service.AnalysisService;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.config.GeminiProperties;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.document.repository.InMemoryDocumentRepository;
import com.nayalens.document.service.DocumentParserService;
import com.nayalens.document.service.SyntheticDemoProvider;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionPathServiceTest {

    private ActionPathService actionPathService;
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
        DeterministicRiskEngine riskEngine = new DeterministicRiskEngine(verificationService);
        AnalysisService analysisService = new AnalysisService(documentRepository, riskEngine, verificationService, geminiClient, objectMapper);

        actionPathService = new ActionPathService(documentRepository, analysisService, geminiClient, objectMapper);
    }

    @Test
    @DisplayName("Should generate structured ActionPath with procedural steps and lawyer questions")
    void testGenerateActionPath() {
        byte[] bytes = demoProvider.getDemoFileBytes("employment-agreement");
        LegalDocument doc = parserService.parseDocument("Employment.txt", bytes, true);
        documentRepository.save(doc);

        ActionPathResult result = actionPathService.generateActionPath(doc.id());

        assertNotNull(result);
        assertEquals(doc.id(), result.documentId());
        assertNotNull(result.situationOverview());
        assertFalse(result.checklist().isEmpty());
        assertFalse(result.questionsToAskLawyer().isEmpty());
        assertFalse(result.recommendedOfficialResources().isEmpty());

        // Step numbers must be ordered 1..N
        for (int i = 0; i < result.checklist().size(); i++) {
            assertEquals(i + 1, result.checklist().get(i).stepNumber());
        }

        // Official resources must include sovereign legal repositories
        boolean hasNalsa = result.recommendedOfficialResources().stream()
                .anyMatch(r -> r.name().contains("NALSA") || r.officialUrl().contains("nalsa.gov.in"));
        assertTrue(hasNalsa, "Expected NALSA official resource");
    }

    @Test
    @DisplayName("Should throw DocumentNotFoundException for non-existent document")
    void testActionPathNotFound() {
        assertThrows(DocumentNotFoundException.class, () -> actionPathService.generateActionPath("non_existent"));
    }
}
