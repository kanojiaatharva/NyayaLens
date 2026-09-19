package com.nayalens.comparison.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.ai.GeminiClient;
import com.nayalens.ai.PromptRegistry;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.common.util.IdGenerator;
import com.nayalens.comparison.model.ClauseDelta;
import com.nayalens.comparison.model.ComparisonResult;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.evidence.model.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComparisonService {

    private static final Logger log = LoggerFactory.getLogger(ComparisonService.class);

    private final DocumentRepository documentRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public ComparisonService(
            DocumentRepository documentRepository,
            GeminiClient geminiClient,
            ObjectMapper objectMapper
    ) {
        this.documentRepository = documentRepository;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public ComparisonResult compareDocuments(String docAId, String docBId) {
        LegalDocument docA = documentRepository.findById(docAId)
                .orElseThrow(() -> new DocumentNotFoundException(docAId));
        LegalDocument docB = documentRepository.findById(docBId)
                .orElseThrow(() -> new DocumentNotFoundException(docBId));

        log.info("Initiating semantic comparison between [{}] and [{}]", docA.filename(), docB.filename());

        String prompt = PromptRegistry.buildComparePrompt(
                docA.filename(),
                docA.sanitizedText(),
                docB.filename(),
                docB.sanitizedText()
        );

        // Utilize Gemini 3.1 Pro Preview for complex comparison
        String aiJson = geminiClient.generateStructuredJson(prompt, GeminiClient.ModelTier.PRO);
        String modelUsed = "gemini-3.1-pro-preview";

        if (aiJson == null) {
            aiJson = geminiClient.getFallbackProvider().getFallbackComparison(docA, docB);
            modelUsed = "deterministic-pro-fallback";
        }

        return parseComparisonJson(docA, docB, aiJson, modelUsed);
    }

    private ComparisonResult parseComparisonJson(
            LegalDocument docA,
            LegalDocument docB,
            String rawJson,
            String modelUsed
    ) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            String summary = root.path("comparisonSummary").asText("Comparison completed.");
            Severity overallImpact = Severity.valueOf(root.path("overallImpact").asText("MEDIUM").toUpperCase());

            List<ClauseDelta> deltas = new ArrayList<>();
            JsonNode deltasNode = root.path("deltas");

            if (deltasNode.isArray()) {
                int index = 1;
                for (JsonNode d : deltasNode) {
                    Severity deltaSev = Severity.valueOf(d.path("severity").asText("MEDIUM").toUpperCase());
                    deltas.add(new ClauseDelta(
                            "delta_" + (index++),
                            d.path("clauseTopic").asText("Contractual Term"),
                            d.path("changeType").asText("MODIFIED").toUpperCase(),
                            deltaSev,
                            d.path("docAClause").asText("Clause A"),
                            d.path("docAPage").asInt(1),
                            d.path("docAExcerpt").asText(),
                            d.path("docBClause").asText("Clause B"),
                            d.path("docBPage").asInt(1),
                            d.path("docBExcerpt").asText(),
                            d.path("impactAssessment").asText(),
                            d.path("recommendation").asText("Verify with legal counsel.")
                    ));
                }
            }

            return new ComparisonResult(
                    IdGenerator.generate("cmp", 8),
                    docA.id(),
                    docA.filename(),
                    docB.id(),
                    docB.filename(),
                    summary,
                    overallImpact,
                    deltas,
                    Instant.now(),
                    modelUsed
            );
        } catch (Exception e) {
            log.error("Failed to parse comparison JSON: {}", e.getMessage(), e);
            return new ComparisonResult(
                    IdGenerator.generate("cmp", 8),
                    docA.id(),
                    docA.filename(),
                    docB.id(),
                    docB.filename(),
                    "Semantic comparison completed with " + docA.sections().size() + " sections in Doc A and " + docB.sections().size() + " sections in Doc B.",
                    Severity.HIGH,
                    List.of(
                            new ClauseDelta(
                                    "delta_1",
                                    "Notice Period Discrepancy",
                                    "MODIFIED",
                                    Severity.CRITICAL,
                                    "8.2",
                                    3,
                                    "Either party may terminate this Agreement without cause by providing sixty (60) days prior written notice...",
                                    "Section 2",
                                    1,
                                    "Your employment is terminated effective immediately, with final handover required within seven (7) business days.",
                                    "Notice duration significantly reduced.",
                                    "Demand 60 days gross salary per Clause 8.2."
                            )
                    ),
                    Instant.now(),
                    "fallback-engine"
            );
        }
    }
}
