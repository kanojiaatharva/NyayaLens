package com.nayalens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.ai.DeterministicFallbackProvider;
import com.nayalens.ai.GeminiClient;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.comparison.model.ComparisonResult;
import com.nayalens.comparison.service.ComparisonService;
import com.nayalens.config.GeminiProperties;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.document.repository.InMemoryDocumentRepository;
import com.nayalens.document.service.DocumentParserService;
import com.nayalens.document.service.SyntheticDemoProvider;
import com.nayalens.evidence.model.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonServiceTest {

    private ComparisonService comparisonService;
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

        comparisonService = new ComparisonService(documentRepository, geminiClient, objectMapper);
    }

    @Test
    @DisplayName("Should compare Employment Agreement vs Termination Notice and detect notice delta")
    void testCompareEmploymentAndTermination() {
        byte[] bytesA = demoProvider.getDemoFileBytes("employment-agreement");
        byte[] bytesB = demoProvider.getDemoFileBytes("termination-notice");

        LegalDocument docA = parserService.parseDocument("Employment.txt", bytesA, true);
        LegalDocument docB = parserService.parseDocument("Termination.txt", bytesB, true);

        documentRepository.save(docA);
        documentRepository.save(docB);

        ComparisonResult result = comparisonService.compareDocuments(docA.id(), docB.id());

        assertNotNull(result);
        assertEquals(docA.id(), result.documentAId());
        assertEquals(docB.id(), result.documentBId());
        assertEquals(Severity.CRITICAL, result.overallImpact());
        assertFalse(result.deltas().isEmpty());

        boolean hasNoticeDelta = result.deltas().stream()
                .anyMatch(d -> d.clauseTopic().toLowerCase().contains("notice") && d.severity() == Severity.CRITICAL);
        assertTrue(hasNoticeDelta, "Expected critical notice period delta");
    }

    @Test
    @DisplayName("Should throw DocumentNotFoundException when document ID does not exist")
    void testDocumentNotFound() {
        assertThrows(DocumentNotFoundException.class, () ->
                comparisonService.compareDocuments("non_existent_1", "non_existent_2")
        );
    }
}
